package umc.lightup.member.dto;

import lombok.*;
import umc.lightup.member.domain.Member;
import umc.lightup.member.enums.Mbti;
import umc.lightup.member.enums.Role;
import umc.lightup.skill.enums.SkillType;
import umc.lightup.strength.enums.StrengthType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MemberResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDTO {
        long memberId;
        String accessToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO{
        long memberId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyInfoDTO {
        private long id;
        private String name;
        private String nickname;
        private Integer age;
        private Boolean gender;
        private LocalDate birth;
        private Role role;
        private Mbti mbti;
        private String email;
        private String school;
        private String phoneNumber;
        private String selfIntroduce;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileDTO {
        private String profileTitle;
        private String name;
        private String nickname;
        private int age;
        private boolean gender;
        private Mbti mbti;
        private String school;
        private String profileImageUrl;
        private String email;
        private List<SkillResultWithIdDTO> skills; //수정을 위한 ID 반환 필요할 수도 있음
        private List<StrengthResultWithIdDTO> strengths; //수정을 위한 ID 반환 필요할 수도 있음
        private List<RegionResultWithIdDTO> regions; //수정을 위한 ID 반환 필요할 수도 있음
        private List<String> positions; //이건 수정도 String으로 진행하니 이렇게 둠
        private List<PortfolioInfoWithIdDTO> portfolios;
        private String selfIntroduce;
        private List<ActivityInfoDTO> activities;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO {
        private long id;
        private String profileTitle;
        private String name;
        private String nickname;
        private Integer age;
        private Boolean gender;
        private LocalDate birth;
        private Role role;
        private Mbti mbti;
        private String selfIntroduce;
        private String school;
        private List<String> skills;
        private List<String> strengths;
        private List<singleRegionResultDTO> regions;
        private List<String> positions;
        private List<PortfolioInfoDTO> portfolios;
        private List<ActivityInfoDTO> activities;
        private String email;
        private String phoneNumber;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoListDTO {
        List<MemberInfoSimpleDTO> members;
        long numOfTotalResults;
    }

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoSimpleDTO {
        private long id;
        private String name;
        private String nickname;
        private Boolean gender;
        private Mbti mbti;
        private List<String> skills;
        private List<String> strengths;
        private List<singleRegionResultDTO> regions;
        private List<String> positions;
        private String profileImageUrl;
        private boolean liked;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPositionResultDTO {
        private String memberName;
        private String positionName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPositionDeleteResultDTO {
        private String memberName;
        private String deletePositionName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailExistResultDTO{
        boolean exist;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileImageSaveResultDTO {
        String profileImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PortfolioInfoWithIdDTO {
        private long id;
        private String name;
        private String fileUrl;
    }
  
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class selectSkillResultDTO {
        String skillName;
        String memberName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class selectStrengthResultDTO {
        String strengthName;
        String memberName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillResultWithIdDTO {
        long id;
        String name;
        SkillType skillType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrengthResultWithIdDTO {
        long id;
        String name;
        StrengthType strengthType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class selectRegionResultsDTO {
        private List<singleRegionResultDTO> regions;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class singleRegionResultDTO {
        String siDo;
        String siGunGu;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionResultWithIdDTO {
        long id;
        String siDo;
        String siGunGu;
    }

    public static LoginResultDTO.LoginResultDTOBuilder loginResultDTOBuilder() {
        return LoginResultDTO.builder();
    }

    public static JoinResultDTO.JoinResultDTOBuilder joinResultDTOBuilder() {
        return JoinResultDTO.builder();
    }

    public static MemberPositionResultDTO.MemberPositionResultDTOBuilder memberPositionResultDTOBuilder() {
        return MemberPositionResultDTO.builder();
    }

    public static MemberPositionDeleteResultDTO.MemberPositionDeleteResultDTOBuilder memberPositionDeleteResultDTOBuilder() {
        return MemberPositionDeleteResultDTO.builder();
    }
  
    public static MyInfoDTO toMyInfoDTO(Member member) {
        return MyInfoDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .age(member.getAge())
                .role(member.getRole())
                .mbti(Mbti.fromByte(member.getMbti()))
                .birth(member.getBirth())
                .gender(member.getGender())
                .school(member.getSchool())
                .phoneNumber(member.getPhoneNumber())
                .selfIntroduce(member.getSelfIntroduce())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
