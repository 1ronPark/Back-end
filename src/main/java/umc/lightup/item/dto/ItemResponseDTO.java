package umc.lightup.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.lightup.member.enums.Gender;
import umc.lightup.member.enums.Mbti;

import java.time.LocalDateTime;
import java.util.List;

public class ItemResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemJoinResultDTO {
        Long memberId;
        String itemName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResultDTO {
        String itemName;
        String memberName;
        String itemImageUrl;
        private boolean likedByCurrentUser;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResultListDTO {
        List<ItemResultDTO> items;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyItemResultDTO {
        String itemName;
        String introduce;
        String itemImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyItemResultListDTO {
        List<MyItemResultDTO> items;
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
        private boolean gender;
        private int age;
        private Mbti mbti;
        private String email;
        private String school;
        private List<ItemRegionResultDTO> regions;
        private String description;
        private List<RecruitPositionResultDTO> recruitPositions;
        private boolean likedByCurrentUser;
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
    public static class ItemApplyResultDTO {
        private LocalDateTime appliedAt;
        private String message;
    }
}
