package umc.lightup.light_talk.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.light_talk.domain.Post;
import umc.lightup.light_talk.dto.PostRequestDTO;
import umc.lightup.light_talk.dto.PostResponseDTO;
import umc.lightup.member.domain.Member;

import java.util.List;
import java.util.Set;

public interface PostCommandService {
    Post createPost(Member member, PostRequestDTO.PostJoinRequestDTO request, List<MultipartFile> postImages);
    Post getSinglePostWithComments(Long postId);
    List<PostResponseDTO.PostResultDTO> getAllPosts(Pageable pageable, Set<Long> likedPostIds);
    List<PostResponseDTO.MemberPositionDTO> getPostMemberPositions(Post post);
    List<PostResponseDTO.PostImageDTO> getPostImages(Post post);
    List<PostResponseDTO.PostCommentResultDTO> getPostComments(Post post);
    void addPostLike(Member member, Long postId);
    void removePostLike(String email, Long itemId);
    Set<Long> findPostLikes(Long memberId);
}
