package umc.lightup.skill.converter;

import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.dto.SkillResponseDTO;

import java.util.List;

public class SkillConverter {

    public static SkillResponseDTO.skillListDTO toSkillListDTO(List<SkillResponseDTO.skillResultDTO> skillResultDTOList) {
        return SkillResponseDTO.skillListDTO.builder()
                .skills(skillResultDTOList)
                .build();
    }

    public static SkillResponseDTO.skillResultDTO toSkillResultDTO(Skill skill) {
        return SkillResponseDTO.skillResultDTO.builder()
                .skillId(skill.getId())
                .skillName(skill.getName())
                .build();
    }

    //커스텀 스킬 기능 삭제
/*    public static SkillResponseDTO.createdSkillResultDTO toCreatedSkillResultDTO(String skillName, Member member) {
        return SkillResponseDTO.createdSkillResultDTO.builder()
                .createdSkill(skillName)
                .memberId(member.getId())
                .build();
    }

    public static Skill toSkill(SkillRequestDTO.CreateSkillDTO request, Member member) {
        String skillName = request.getSkillName();

        return Skill.createSkill(skillName, true, member);
    }*/
}
