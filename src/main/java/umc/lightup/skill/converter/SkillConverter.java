package umc.lightup.skill.converter;

import umc.lightup.member.domain.Member;
import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.dto.SkillRequestDTO;
import umc.lightup.skill.dto.SkillResponseDTO;

import java.util.List;

public class SkillConverter {

    public static SkillResponseDTO.skillListDTO toSkillListDTO(List<String> skills) {
        return SkillResponseDTO.skillListDTO.builder()
                .skills(skills)
                .build();
    }

    public static SkillResponseDTO.createdSkillResultDTO toCreatedSkillResultDTO(String skillName, Member member) {
        return SkillResponseDTO.createdSkillResultDTO.builder()
                .createdSkill(skillName)
                .memberId(member.getId())
                .build();
    }

    public static Skill toSkill(SkillRequestDTO.CreateSkillDTO request, Member member) {
        String skillName = request.getSkillName();

        return Skill.createSkill(skillName, true, member);
    }
}
