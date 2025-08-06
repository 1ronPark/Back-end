package umc.lightup.member.converter;

import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberRegion;
import umc.lightup.member.domain.Portfolio;
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
}
