package umc.lightup.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.lightup.item.enums.ItemApplyStatus;
import umc.lightup.member.enums.Mbti;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ItemResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemJoinResultDTO {
        private Long memberId;
        private String itemName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemChangeResultDTO {
        private Long memberId;
        private String itemName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResultDTO {
        private Long itemId;
        private String itemName;
//        private String schoolName;
        private String memberName;
        private String memberProfileImageUrl;
        private String itemImageUrl;
        private LocalDate updatedAt;
        private boolean recruitStatus;
        private Long viewCount;
        private int commentCount;
        private boolean likedByCurrentUser;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResultListDTO {
        private List<ItemResultDTO> items;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyItemResultDTO {
        private Long itemId;
        private String itemName;
        private String introduce;
        private String itemImageUrl;
        private List<ItemCategoriesResultDTO> itemCategories;
        private boolean recruitStatus;
        private boolean applicantStatus;
        private boolean isMyApplyItem;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyItemResultListDTO {
        private List<MyItemResultDTO> items;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemInfoDTO {
        private String introduce;
        private String itemName;
        private String itemProfileImageUrl;
        private String memberName;
        private String nickName;
        private boolean gender;
        private int age;
        private Mbti mbti;
        private String email;
        private String school;
        private List<ItemRegionResultDTO> regions;
        private String description;
        private List<RecruitPositionResultDTO> recruitPositions;
        private List<ItemCategoriesResultDTO> itemCategories;
        private List<ItemCommentResultDTO> itemComments;
        private int commentCount;
        private LocalDate updatedAt;
        private boolean likedByCurrentUser;
        private boolean applicantStatus;
        private boolean suggestStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRegionResultDTO {
        private String siDo;
        private String siGunGu;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecruitPositionResultDTO {
        private String positionName;
        private Integer recruitNumber;
        private String mainTasks;
        private String preferentialTreatment;
        private String preferMbti;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemCategoriesResultDTO {
        private String categoryName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemApplyResultDTO {
        private LocalDateTime appliedAt;
        private String message;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemApplyStatusListDTO {
        private List<ItemApplyStatusDTO> itemApplyStatuses;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemApplyStatusDTO {
        private boolean itemOwned;
        private long itemId;
        private String itemName;
        private String itemImageUrl;
        private String itemOwnerUsername;
        private Long memberId;
        private String memberUsername;
        private String memberProfileImageUrl;
        private long applyId;
        private boolean fromOwner;
        private ItemApplyStatus applyStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOfferResultDTO {
        private LocalDateTime appliedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemCommentResultDTO {
        private Long itemCommentId;
        private String authorName;
        private String authorProfileImageURL;
        private String content;
        private LocalDateTime updatedAt;
    }
}
