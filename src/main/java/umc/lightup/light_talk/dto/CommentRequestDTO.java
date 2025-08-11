package umc.lightup.light_talk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class CommentRequestDTO {

    @Getter
    @Setter
    public static class CommentJoinDTO {
        @NotBlank
        private String content;
    }
}
