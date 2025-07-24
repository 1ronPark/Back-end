package umc.lightup.member.dto;

import lombok.*;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.Portfolio;
import umc.lightup.region.domain.Region;

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
    private List<String> skills;
    private List<String> strengths;
    private List<Region> regions;
    private List<Portfolio> portfolios;
    private boolean emailOpen = false;
    private boolean phoneOpen = false;
    private boolean pictureOpen = false;

    public MemberResponseDTO.MemberInfoDTO toMemberInfoDTO() {
        return MemberResponseDTO.MemberInfoDTO.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .age(member.getAge())
                .role(member.getRole())
                .mbti(member.getMbti())
                .birth(member.getBirth())
                .gender(member.getGender())
                .school(member.getSchool())
                .career(member.getCareer())

                .skills(skills)
                .strengths(strengths)
                .regions(regions.stream()
                        .map(r->r.getSiDo()+" "+r.getSiGunGu())
                        .toList())
                .portfolios(portfolios.stream()
                        .map(portfolio -> MemberResponseDTO.PortfolioInfoDTO.builder()
                                .name(portfolio.getName())
                                .fileUrl(portfolio.getFileUrl())
                                .build())
                        .toList())

                .email(emailOpen?member.getEmail():null)
                .phoneNumber(phoneOpen?member.getPhoneNumber():null)
                .profileImageUrl(pictureOpen?member.getProfileImageUrl():null)
                .build();
    }
}