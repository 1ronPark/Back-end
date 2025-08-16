package umc.lightup.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class OAuth2ResponseDTO {
    @Getter
    @Setter
    public static class GoogleOAuth2ResponseDTO {
        String access_token;
//        String refresh_token;
        int expires_in;
        String scope;
        String token_type;
        String id_token;
//        String id_token_hint;
//        String refresh_token_hint;
//        String scope_hint;
//        String token_hint;
    }

    @Getter
    @Setter
    public static class GoogleUserinfoResponseDTO {
        String sub;
        String id;
        String email;
        String verified_email;
        String name;
        String given_name;
        String family_name;
        String picture;
        String locale;
    }

    @Getter
    @Setter
    public static class KakaoOAuth2ResponseDTO {
        private String token_type;
        private String access_token;
        private String expires_in;
        private String refresh_token;
        private String refresh_token_expires_in;
    }

    @Getter
    @Setter
    public static class KakaoUserinfoResponseDTO {
//        private String iss;
//        private String aud;
//        private String sub;
//        private String iat;
//        private String exp;
//        private String auth_time;
//        private String nickname;
//        private String picture;
//        private String email;
        long id;
        LocalDateTime connected_at;
        Properties properties;
        KakaoAccount kakao_account;

        @Getter
        @Setter
        public static class Properties {
            private String nickname;
            private String profile_image;
            private String thumbnail_image;
        }

        @Getter
        @Setter
        public static class KakaoAccount {
            Boolean profile_nickname_needs_agreement;
            Boolean profile_image_needs_agreement;
            Profile profile;
            Boolean has_email;
            Boolean email_needs_agreement;
            Boolean is_email_valid;
            Boolean is_email_verified;
            String email;
        }

        @Getter
        @Setter
        public static class Profile {
            String nickname;
            String thumbnail_image_url;
            String profile_image_url;
            Boolean is_default_image;
            Boolean is_default_nickname;
        }

        public String getEmail() {
            return kakao_account.getEmail();
        }
    }
}