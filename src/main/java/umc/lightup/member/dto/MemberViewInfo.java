package umc.lightup.member.dto;

import lombok.*;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.Activity;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberRegion;
import umc.lightup.member.domain.Portfolio;
import umc.lightup.skill.domain.Skill;
import umc.lightup.strength.domain.Strength;

import java.util.List;

/**
 * Member의 정보와 이 정보가 어디까지 보이는지에 대한 정보.
 * MemberInfoDTO를 만들기 위한 DTO.
 * DTO를 위한 DTO를 따로 뺀 이유는 저 DTO를 만드는 데 너무 많은 인자가 필요해서임.
 * Builder pattern 쓰는 이유가 인자 순서에 문제 없다는 건데 이 의미를 살리기 위함.
 * 물론 본인도 처음 써보는 방식이라 적절한 설계인지는 생각이 필요함.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberViewInfo {
    private Member member;
    @Builder.Default
    private List<String> positionNames = List.of();
    @Builder.Default
    private List<String> skillNames = List.of();
    @Builder.Default
    private List<String> strengthNames = List.of();
    @Builder.Default
    private List<Skill> skills = List.of();
    @Builder.Default
    private List<Strength> strengths = List.of();
    @Builder.Default
    private List<MemberRegion> regions = List.of();
    @Builder.Default
    private List<Portfolio> portfolios = List.of();
    @Builder.Default
    private List<Activity> activities = List.of();
    @Builder.Default
    private boolean emailOpen = false;
    @Builder.Default
    private boolean phoneOpen = false;
    @Builder.Default
    private boolean pictureOpen = false;

    public MemberResponseDTO.MemberInfoDTO toMemberInfoDTO() {
        return MemberResponseDTO.MemberInfoDTO.builder()
                .id(member.getId())
                .profileTitle(member.getProfileTitle())
                .name(member.getName())
                .nickname(member.getNickname())
                .age(member.getAge())
                .role(member.getRole())
                .mbti(member.getMbti())
                .birth(member.getBirth())
                .gender(member.getGender())
                .school(member.getSchool())
                .selfIntroduce(member.getSelfIntroduce())

                .skills(skillNames)
                .strengths(strengthNames)
                .positions(positionNames)
                .regions(regions.stream()
                        .map(MemberConverter::toSingleRegionResultDTO)
                        .toList())
                .portfolios(getPortfolioInfoDTOs())
                .activities(getActivityInfoDTOs())

                .email(emailOpen?member.getEmail():null)
                .phoneNumber(phoneOpen?member.getPhoneNumber():null)
                .profileImageUrl(pictureOpen?member.getProfileImageUrl():null)
                .build();
    }

    private List<ActivityInfoDTO> getActivityInfoDTOs() {
        return activities.stream()
                .map(activity -> ActivityInfoDTO.builder()
                        .name(activity.getName())
                        .startDate(activity.getStartDate())
                        .hasEndDate(activity.getEndDate() != null)
                        .endDate(activity.getEndDate())
                        .build())
                .toList();
    }

    private List<PortfolioInfoDTO> getPortfolioInfoDTOs() {
        return portfolios.stream()
                .map(portfolio -> PortfolioInfoDTO.builder()
                        .name(portfolio.getName())
                        .fileUrl(portfolio.getFileUrl())
                        .build())
                .toList();
    }

    private List<String> getRegionAsList() {
        return regions.stream()
                .map(r -> r.getSiDo() + " " + r.getSiGunGu())
                .toList();
    }

    public MemberResponseDTO.MyProfileDTO toMyProfileDTO() {
        return MemberResponseDTO.MyProfileDTO.builder()
                .profileTitle(member.getProfileTitle())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .age(member.getAge())
                .gender(member.getGender())
                .school(member.getSchool())
                .mbti(member.getMbti())
                .profileImageUrl(member.getProfileImageUrl())
                .selfIntroduce(member.getSelfIntroduce())
                .skills(skills.stream()
                        .map(s->MemberResponseDTO.SkillResultWithIdDTO.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .skillType(s.getSkillType())
                                .build())
                        .toList())
                .strengths(strengths.stream()
                        .map(s->MemberResponseDTO.StrengthResultWithIdDTO.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .strengthType(s.getStrengthType())
                                .build())
                        .toList())
                .regions(regions.stream()
                        .map(r-> MemberResponseDTO.RegionResultWithIdDTO.builder()
                                .id(r.getId())
                                .siDo(r.getSiDo())
                                .siGunGu(r.getSiGunGu())
                                .build())
                        .toList())
                .positions(positionNames)
                .portfolios(getPortfolioInfoDTOs())
                .activities(getActivityInfoDTOs())
                .build();
    }
}