package umc.lightup.item.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
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
    public static class ItemCategoryRequestDTO {
        @NotBlank
        private String itemCategory;
    }

    @Getter
    @Setter
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
}
