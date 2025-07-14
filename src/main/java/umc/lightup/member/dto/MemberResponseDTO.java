package umc.lightup.member.dto;

import lombok.*;
import umc.lightup.member.domain.Member;
import umc.lightup.member.enums.Mbti;
import umc.lightup.member.enums.Role;

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
    public static class MemberInfoDTO {
        private String name;
        private String nickname;
        private int age;
        private boolean gender;
        private LocalDate birth;
        private Role role;
        private Mbti mbti;
        private String career;
        private String school;
        private List<String> skills;
        private List<String> strengths;
        private List<String> regions;
        private List<PortfolioInfoDTO> portfolios;
        private String email;
        private String phoneNumber;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioInfoDTO {
        private String name;
        private String fileUrl;
    }

    public static LoginResultDTO.LoginResultDTOBuilder loginResultDTOBuilder() {
        return LoginResultDTO.builder();
    }

    public static JoinResultDTO.JoinResultDTOBuilder joinResultDTOBuilder() {
        return JoinResultDTO.builder();
    }

    public static MyInfoDTO toMyInfoDTO(Member member) {
        return MyInfoDTO.builder()
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