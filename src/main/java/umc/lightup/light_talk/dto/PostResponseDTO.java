package umc.lightup.light_talk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostJoinResultDTO {
        private Long postId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInfoDTO {
        private String authorName;
        private List<MemberPositionDTO> positionName;
        private String schoolName;
        private String authorProfileImageURL;
        private String content;
        private List<PostImageDTO> postImages;
        private LocalDateTime createdAt;
        private List<PostCommentResultDTO> postComments;
        private Long postLikes;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPositionDTO {
        private String positionName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostImageDTO {
        private String postImageURL;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCommentResultDTO {
        private Long postCommentId;
        private String authorName;
        private String authorProfileImageURL;
        private String content;
        private LocalDateTime updatedAt;
        private Long commentLikeCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResultDTO {
        private Long postId;
        private String authorName;
        private List<MemberPositionDTO> positionName;
        private String schoolName;
        private String authorProfileImageURL;
        private String content;
        private List<PostImageDTO> postImages;
        private LocalDateTime createdAt;
        private int postCommentCount;
        private Long postLikeCount;
        private boolean likedByCurrentUser;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResultListDTO {
        private List<PostResultDTO> posts;
    }
}
