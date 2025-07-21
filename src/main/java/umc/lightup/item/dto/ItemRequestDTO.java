package umc.lightup.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ItemRequestDTO {

    @Getter
    @Setter
    public static class ItemJoinRequestDTO {
        @NotBlank
        private String name;
        @NotBlank
        private String introduce;
        @NotBlank
        private String projectStatus;
        @NotBlank
        private String collaboration;
        @NotBlank
        private String address;
        @NotNull
        private boolean office;
        @NotBlank
        private String preferMbti;
        @NotBlank
        private String description;
    }
}
