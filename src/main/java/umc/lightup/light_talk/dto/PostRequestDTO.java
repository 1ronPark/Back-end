package umc.lightup.light_talk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class PostRequestDTO {

    @Getter
    @Setter
    public static class PostJoinRequestDTO {
        @NotBlank
        private String content;
    }
}
