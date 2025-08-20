package umc.lightup.item.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import umc.lightup.position.validation.annotation.ExistPosition;
import umc.lightup.region.validation.annotation.ExistSiDo;
import umc.lightup.region.validation.annotation.ExistSiGunGu;

import java.util.List;

public class ItemRequestDTO {

    @Getter
    @Setter
    public static class ItemJoinRequestDTO {
        @NotBlank
        private String name;
        @NotBlank
        private String introduce;
        private String extraLink1;
        private String extraLink2;
        @NotBlank
        private String description;
        @Size(max = 3, message = "프로젝트 카테고리는 최대 3개까지 선택할 수 있습니다.")
        @Valid
        private List<ItemCategoryRequestDTO> itemCategories;
        private boolean projectStatus;
        @Size(max = 3, message = "협업 지역은 최대 3개까지 선택할 수 있습니다.")
        @Valid
        private List<CollaborationRegionRequestDTO> collaborationRegions;
        @NotEmpty(message = "모집 포지션을 하나 이상 선택해야 합니다.")
        @Valid
        private List<RecruitPositionRequestDTO> recruitPositions;
    }

    @Getter
    @Setter
    public static class ItemChangeRequestDTO {
        private String name;
        private String introduce;
        private String extraLink1;
        private String extraLink2;
        private String description;
        @Size(max = 3, message = "프로젝트 카테고리는 최대 3개까지 선택할 수 있습니다.")
        @Valid
        private List<ItemCategoryRequestDTO> itemCategories;
        private Boolean projectStatus;
        @Size(max = 3, message = "협업 지역은 최대 3개까지 선택할 수 있습니다.")
        @Valid
        private List<CollaborationRegionRequestDTO> collaborationRegions;
        @Valid
        private List<RecruitPositionRequestDTO> recruitPositions;
    }

    @Getter
    @Setter
    public static class ItemCategoryRequestDTO {
        @NotBlank
        private String itemCategory;
    }

    @Getter
    @Setter
    @Builder
    public static class CollaborationRegionRequestDTO {
        @NotBlank
        @ExistSiDo
        private String siDo;
        @ExistSiGunGu
        private String siGunGu;
    }

    @Getter
    @Setter
    public static class RecruitPositionRequestDTO {
        @NotNull
        @ExistPosition
        private Long positionId;
        @NotBlank
        private String mainTasks;
        private String preferentialTreatment;
        private String preferMbti;
        @NotNull
        private Integer recruitNumber;
    }

    @Getter
    @Setter
    public static class AcceptItemApplyRequestDTO {
        @NotNull
        private Long itemApplyId; //재지원 기능을 만들지는 모르겠으나 만드려면 이걸로 받아야 함
//        @NotNull
//        private Long itemId;
//        @NotNull
//        // 왜 내가 ExistMember를 안 만들었지??? 물론 만들면 성능은 떨어짐
//        private Long memberId;
        @NotNull
        private Boolean accept;
    }

    @Getter
    @Setter
    public static class OfferPositionRequestDTO {
        @NotNull
        private Long itemId;
        @NotNull
        // 왜 내가 ExistMember를 안 만들었지??? 물론 만들면 성능은 떨어짐
        private Long memberId;
    }

    @Getter
    @Setter
    public static class AcceptItemOfferRequestDTO {
        @NotNull
        private Long itemApplyId; //재지원 기능을 만들지는 모르겠으나 만드려면 이걸로 받아야 함
        @NotNull
        private Boolean accept;
    }

    @Getter
    @Setter
    public static class ItemCommentRequestDTO {
        @NotBlank
        private String content;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemSearchRequestDTO {
        private String category;
        private Long positionId;
        private List<CollaborationRegionRequestDTO> itemRegions;
        @Positive
        private Long page;
        @Positive
        private Long limit;
    }
}
