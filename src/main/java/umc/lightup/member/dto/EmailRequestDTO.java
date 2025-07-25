package umc.lightup.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EmailRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    // 템플릿 이름: password-reset
    public static class PasswordInitializeDTO {
        String userName;
        String tempPassword;
    }
}