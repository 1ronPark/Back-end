package umc.lightup.light_talk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.aws.s3.AmazonS3Manager;
import umc.lightup.common.Uuid;
import umc.lightup.common.repository.UuidRepository;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.light_talk.converter.PostConverter;
import umc.lightup.light_talk.domain.Post;
import umc.lightup.light_talk.domain.PostImage;
import umc.lightup.light_talk.domain.PostLike;
import umc.lightup.light_talk.dto.PostRequestDTO;
import umc.lightup.light_talk.dto.PostResponseDTO;
import umc.lightup.light_talk.repository.CommentRepository;
import umc.lightup.light_talk.repository.PostImageRepository;
import umc.lightup.light_talk.repository.PostLikeRepository;
import umc.lightup.light_talk.repository.PostRepository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.repository.MemberPositionRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommandServiceImpl implements PostCommandService {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final PostLikeRepository postLikeRepository;

    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Override
    @Transactional
    public Post createPost(Member member, PostRequestDTO.PostJoinRequestDTO request, List<MultipartFile> postImages) {
        Post post = Post.builder()
                .content(request.getContent())
                .postMember(member)
                .build();
        Post savedPost = postRepository.save(post);

        if (postImages != null && !postImages.isEmpty()) {
            for (MultipartFile image : postImages) {

                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = uuidRepository.save(Uuid.builder()
                        .uuid(uuid).build());

                String postImageURL = s3Manager.uploadFile(s3Manager.generatePostImageKeyName(savedUuid), image);

                PostImage postImage = PostImage.builder()
                        .post(post)
                        .imageUrl(postImageURL)
                        .build();

                postImageRepository.save(postImage);
            }
        }

        return savedPost;
    }

    @Override
    public Post getSinglePostWithComments(Long postId) {
        return postRepository.findByIdWithCommentsAndCommentMembers(postId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POST_NOT_FOUND));
    }

    @Override
    public List<PostResponseDTO.MemberPositionDTO> getPostMemberPositions(Post post) {
        Member postMember = post.getPostMember();
        return memberPositionRepository.findPositionNameByMember(postMember).stream()
                .map(PostConverter::toMemberPositionDTO)
                .toList();
    }

    @Override
    public List<PostResponseDTO.PostImageDTO> getPostImages(Post post) {
        return postImageRepository.findByPost(post).stream()
                .map(PostConverter::toPostImageDTO)
                .toList();
    }

    @Override
    public List<PostResponseDTO.PostCommentResultDTO> getPostComments(Post post) {
        return post.getPostComments().stream()
                .map(PostConverter::toPostCommentResultDTO)
                .toList();
    }

    @Override
    @Transactional
    public void addPostLike(Member member, Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.POST_NOT_FOUND));

        if (findPost.getPostMember().equals(member)) {
            throw new GeneralHandler(ErrorStatus.MY_POST_LIKE);
        }

        if (postLikeRepository.existsByMemberIdAndPostId(member.getId(), postId)) {
            throw new GeneralHandler(ErrorStatus.POST_ALREADY_LIKED);
        }

        if (postRepository.increasePostLike(postId) == 0) {
            throw new GeneralHandler(ErrorStatus.POST_NOT_FOUND);
        }

        postLikeRepository.save(PostLike.builder()
                .post(findPost)
                .member(member)
                .build());
    }

    @Override
    @Transactional
    public void removePostLike(String email, Long postId) {
        if (postLikeRepository.removeByMemberEmailAndPostId(email, postId) == 0) {
            throw new GeneralHandler(ErrorStatus.POST_LIKE_NOT_FOUND);
        }

        if (postRepository.decreasePostLike(postId) == 0) {
            throw new GeneralHandler(ErrorStatus.POST_LIKE_NOT_FOUND);
        }
    }

    @Override
    public Set<Long> findPostLikes(Long memberId) {
        return postLikeRepository.findPostIdsLikedByMemberId(memberId);
    }

    @Override
    public List<PostResponseDTO.PostResultDTO> getAllPosts(Pageable pageable, Set<Long> likedPostIds) {
        Page<Post> postPage = postRepository.findAllWithDetails(pageable);

        return postPage.stream()
                .map(post -> {
                    boolean liked = likedPostIds != null && likedPostIds.contains(post.getId());
                    int commentCount = post.getPostComments().size();
                    Member postMember = post.getPostMember();

                    List<PostResponseDTO.MemberPositionDTO> memberPositionDTOList = memberPositionRepository.findPositionNameByMember(postMember).stream()
                            .map(PostConverter::toMemberPositionDTO)
                            .toList();

                    List<PostResponseDTO.PostImageDTO> postImageDTOList = post.getPostImages().stream()
                            .map(PostConverter::toPostImageDTO)
                            .toList();

                    return PostConverter.toPostResultDTO(post, memberPositionDTOList, postImageDTOList, commentCount, liked);
                }).toList();
    }
}
