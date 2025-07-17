package umc.lightup.member.converter;

import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberResponseDTO;

public class MemberConverter {

    public static MemberResponseDTO.selectSkillResultDTO toSelectSkillResultDTO(String skillName, Member member) {
        return MemberResponseDTO.selectSkillResultDTO.builder()
                .skillName(skillName)
                .memberName(member.getName())
                .build();
    }
}
