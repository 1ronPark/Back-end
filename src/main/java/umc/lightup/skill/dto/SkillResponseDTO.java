package umc.lightup.skill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SkillResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class skillListDTO {
        List<skillResultDTO> skills;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class skillResultDTO {
        private Long skillId;
        private String skillName;
    }

    //커스텀 스킬 기능 삭제
/*    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createdSkillResultDTO {
        String createdSkill;
        Long memberId;
    }*/

}
