package umc.lightup.member.converter;

import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberResponseDTO;

import java.util.List;

public class MemberConverter {

    public static MemberResponseDTO.selectSkillResultDTO toSelectSkillResultDTO(String skillName, Member member) {
        return MemberResponseDTO.selectSkillResultDTO.builder()
                .skillName(skillName)
                .memberName(member.getName())
                .build();
    }

    public static MemberResponseDTO.selectStrengthResultDTO toSelectStrengthResultDTO(String strengthName, Member member) {
        return MemberResponseDTO.selectStrengthResultDTO.builder()
                .strengthName(strengthName)
                .memberName(member.getName())
                .build();
    }

    public static MemberResponseDTO.CredentialInfoResultDTO toSelectCredentialInfoResultDTO(List<Credential> credentials) {
        return MemberResponseDTO.CredentialInfoResultDTO.builder()
                .credentials(credentials.stream()
                        .map(c->MemberResponseDTO.CredentialInfoDTO.builder()
                                .credentialType(c.getCredentialType())
                                .createdAt(c.getCreatedAt())
                                .updatedAt(c.getUpdatedAt())
                                .build())
                        .toList())
                .build();
    }
}
