package umc.lightup.strength.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class StrengthRequestDTO {

    @Getter
    @Setter
    public static class CreateStrengthDTO {
        @NotBlank
        String strengthName;
    }
}
