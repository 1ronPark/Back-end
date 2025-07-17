package umc.lightup.skill.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class SkillRequestDTO {

    @Getter
    @Setter
    public static class CreateSkillDTO {
        @NotBlank
        String skillName;
    }
}
