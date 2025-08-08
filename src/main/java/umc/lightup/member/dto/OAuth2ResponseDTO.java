package umc.lightup.member.dto;

import lombok.Getter;
import lombok.Setter;

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
    public static class KakaoUserinfoResponseDTO {
        long id;
        KakaoAccount kakao_account;

        @Getter
        @Setter
        public static class KakaoAccount {
            String email;
            Profile profile;
        }

        @Getter
        @Setter
        public static class Profile {
            String nickname;
            String profile_image_url;
        }

        public String getEmail() {
            return kakao_account.getEmail();
        }
    }

    @Getter
    @Setter
    public static class KakaoOAuth2ResponseDTO {
        private String access_token;
        // ...
    }
}