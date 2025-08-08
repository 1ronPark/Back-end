package umc.lightup.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OAuth2RequestDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoogleOAuth2RequestDTO {
        String code;
        String client_id;
        String client_secret;
        String redirect_uri;
        String grant_type;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoOAuth2RequestDTO {
        String grant_type;
        String client_id;
        String redirect_uri;
        String code;
        String client_secret;
    }
}