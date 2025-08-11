package umc.lightup.light_talk.converter;

import umc.lightup.light_talk.domain.Comment;
import umc.lightup.light_talk.domain.Post;
import umc.lightup.light_talk.domain.PostImage;
import umc.lightup.light_talk.dto.PostResponseDTO;
import umc.lightup.member.domain.MemberPosition;

import java.util.List;

public class PostConverter {

    public static PostResponseDTO.PostJoinResultDTO toPostJoinResultDTO(Post post) {
        return PostResponseDTO.PostJoinResultDTO.builder()
                .postId(post.getId())
                .build();
    }

    public static PostResponseDTO.PostInfoDTO toPostInfoDTO (Post post, List<PostResponseDTO.MemberPositionDTO> memberPositionDTOList, List<PostResponseDTO.PostImageDTO> postImageDTOList, List<PostResponseDTO.PostCommentResultDTO> postCommentResultDTOList) {
        return PostResponseDTO.PostInfoDTO.builder()
                .authorName(post.getPostMember().getName())
                .positionName(memberPositionDTOList)
                .schoolName(post.getPostMember().getSchool())
                .authorProfileImageURL(post.getPostMember().getProfileImageUrl())
                .content(post.getContent())
                .postImages(postImageDTOList)
                .createdAt(post.getCreatedAt())
                .postComments(postCommentResultDTOList)
                .postLikes(post.getLikes())
                .build();
    }

    public static PostResponseDTO.PostCommentResultDTO toPostCommentResultDTO (Comment postComment) {
        return PostResponseDTO.PostCommentResultDTO.builder()
                .postCommentId(postComment.getId())
                .authorName(postComment.getCommentMember().getName())
                .authorProfileImageURL(postComment.getCommentMember().getProfileImageUrl())
                .content(postComment.getContent())
                .updatedAt(postComment.getUpdatedAt())
                .commentLikeCount(postComment.getLikes())
                .build();
    }

    public static PostResponseDTO.MemberPositionDTO toMemberPositionDTO (String positionName) {
        return PostResponseDTO.MemberPositionDTO.builder()
                .positionName(positionName)
                .build();
    }

    public static PostResponseDTO.MemberPositionDTO toMemberPositionDTO (MemberPosition memberPosition) {
        return PostResponseDTO.MemberPositionDTO.builder()
                .positionName(memberPosition.getPosition().getName())
                .build();
    }

    public static PostResponseDTO.PostImageDTO toPostImageDTO (PostImage postImage) {
        return PostResponseDTO.PostImageDTO.builder()
                .postImageURL(postImage.getImageUrl())
                .build();
    }

    public static PostResponseDTO.PostResultDTO toPostResultDTO (Post post, List<PostResponseDTO.MemberPositionDTO> memberPositionDTOList, List<PostResponseDTO.PostImageDTO> postImageDTOList, int postCommentCount, boolean postLike) {
        return PostResponseDTO.PostResultDTO.builder()
                .postId(post.getId())
                .authorName(post.getPostMember().getName())
                .positionName(memberPositionDTOList)
                .schoolName(post.getPostMember().getSchool())
                .authorProfileImageURL(post.getPostMember().getProfileImageUrl())
                .content(post.getContent())
                .postImages(postImageDTOList)
                .createdAt(post.getCreatedAt())
                .postCommentCount(postCommentCount)
                .postLikeCount(post.getLikes())
                .likedByCurrentUser(postLike)
                .build();
    }

    public static PostResponseDTO.PostResultListDTO toPostResultListDTO (List<PostResponseDTO.PostResultDTO> postResultDTOList) {
        return PostResponseDTO.PostResultListDTO.builder()
                .posts(postResultDTOList)
                .build();
    }
}
