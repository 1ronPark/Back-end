package umc.lightup.lighttalk.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.lighttalk.domain.Post;
import umc.lightup.lighttalk.dto.PostRequestDTO;
import umc.lightup.lighttalk.dto.PostResponseDTO;
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
    void removePost(Member member, Long postId);
    void addPostLike(Member member, Long postId);
    void removePostLike(String email, Long itemId);
    Set<Long> findPostLikes(Long memberId);
}
