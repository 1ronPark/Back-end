package umc.lightup.member.dto;

import lombok.*;
import umc.lightup.member.domain.Member;
import umc.lightup.member.enums.Mbti;
import umc.lightup.member.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    public static class MemberInfoDTO {
        private long id;
        private String name;
        private String nickname;
        private int age;
        private boolean gender;
        private LocalDate birth;
        private Role role;
        private Mbti mbti;
        private String email;
        private String school;
        private String phoneNumber;
        private String career;
        private String profileImageUrl;
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


    public static MemberInfoDTO toMemberInfoDTO(Member member) {
        return MemberInfoDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .age(member.getAge())
                .role(member.getRole())
                .mbti(member.getMbti())
                .birth(member.getBirth())
                .gender(member.getGender())
                .school(member.getSchool())
                .phoneNumber(member.getPhoneNumber())
                .career(member.getCareer())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}