package umc.lightup.member.converter;

import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberRegion;
import umc.lightup.member.domain.Portfolio;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.enums.Mbti;

import java.util.List;

public class MemberConverter {

    public static MemberResponseDTO.MyInfoDTO toMyInfoDTO(Member member) {
        return MemberResponseDTO.MyInfoDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .age(member.getAge())
                .role(member.getRole())
                .mbti(Mbti.fromByte(member.getMbti()))
                .birth(member.getBirth())
                .gender(member.getGender())
                .school(member.getSchool() == null ? null : member.getSchool().getName())
                .phoneNumber(member.getPhoneNumber())
                .selfIntroduce(member.getSelfIntroduce())
                .profileImageUrl(member.getProfileImageUrl())
                .profileTitle(member.getProfileTitle())
                .build();
    }

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

    public static MemberResponseDTO.selectRegionResultsDTO toSelectRegionResultsDTO(List<MemberResponseDTO.singleRegionResultDTO> regionList) {
        return MemberResponseDTO.selectRegionResultsDTO.builder()
                .regions(regionList)
                .build();
    }

    public static MemberResponseDTO.singleRegionResultDTO toSingleRegionResultDTO (MemberRegion memberRegion) {
        return MemberResponseDTO.singleRegionResultDTO.builder()
                .siDo(memberRegion.getSiDo())
                .siGunGu(memberRegion.getSiGunGu())
                .build();
    }

    public static MemberResponseDTO.PortfolioInfoWithIdDTO toPortfolioInfoWithIdDTO(Portfolio portfolio) {
        return MemberResponseDTO.PortfolioInfoWithIdDTO.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .fileUrl(portfolio.getFileUrl())
                .build();
    }

    public static MemberResponseDTO.MemberHistoryInfoListDTO toMemberHistoryInfoListDTO(List<MemberResponseDTO.HistoryInfoDTO> memberHistory) {
        return MemberResponseDTO.MemberHistoryInfoListDTO.builder()
                .memberHistory(memberHistory)
                .build();
    }
}
