package umc.lightup.member.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.enums.Mbti;
import umc.lightup.member.enums.Role;
import umc.lightup.member.validation.annotation.UniqueEmail;
import umc.lightup.member.validation.annotation.UniqueNickname;
import umc.lightup.member.validation.annotation.UniquePhoneNumber;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MemberRequestDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class JoinDto{
        @NotEmpty
        private String name;
        @UniqueNickname
        private String nickname;
        @NotNull
        private Boolean gender;
        @NotNull
        @Past
        private LocalDate birth;
        @NotNull
        private Role role;
        @NotNull
        private Mbti mbti;
        @NotEmpty
        @Email
        @UniqueEmail
        private String email;
        private String school;
        @NotEmpty
        @UniquePhoneNumber
        @Pattern(regexp = "0[1-8]\\d{0,1}-\\d{3,4}-\\d{4,5}")
        private String phoneNumber;
        @NotEmpty
        private String password;

        public Member toMember(){
            return Member.builder()
                    .name(this.name)
                    .nickname(this.nickname)
                    .gender(this.gender)
                    .birth(this.birth)
                    .role(this.role)
                    .mbti(this.mbti)
                    .email(this.email)
                    .school(this.school)
                    .phoneNumber(this.phoneNumber)
                    .age((int) this.birth.until(LocalDate.now(), ChronoUnit.YEARS))
                    .build();
        }

        public Credential toCredential(PasswordEncoder passwordEncoder){
            return Credential.builder()
                    .credential(passwordEncoder.encode(this.password))
                    .credentialType(CredentialType.PASSWORD)
                    .member(this.toMember())
                    .build();
        }
    }

    // Unique check을 하지 않은 상태
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChangeDto{
        @NotEmpty
        private String name;
        private String nickname;
        @NotNull
        private Boolean gender;
        @NotNull
        @Past
        private LocalDate birth;
        @NotNull
        private Role role;
        @NotNull
        private Mbti mbti;
        @NotEmpty
        @Email
        private String email;
        private String school;
        @NotEmpty
        @Pattern(regexp = "0[1-8]\\d{0,1}-\\d{3,4}-\\d{4,5}")
        private String phoneNumber;

        public Member toMember(long id){
            return Member.builder()
                    .id(id)
                    .name(this.name)
                    .nickname(this.nickname)
                    .gender(this.gender)
                    .birth(this.birth)
                    .role(this.role)
                    .mbti(this.mbti)
                    .email(this.email)
                    .school(this.school)
                    .phoneNumber(this.phoneNumber)
                    .age((int) this.birth.until(LocalDate.now(), ChronoUnit.YEARS))
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PasswordLoginRequestDTO{
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;

        @NotBlank(message = "패스워드는 필수입니다.")
        private String password;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PasswordChangeRequestDTO{
        @NotBlank(message = "기존 패스워드는 필수입니다.")
        private String prevPassword;

        @NotBlank(message = "새로운 패스워드는 필수입니다.")
        private String newPassword;
    }

    @Getter
    @Setter
    public static class MemberPositionRequestDTO {

        @NotEmpty
        private String position;
    }
}