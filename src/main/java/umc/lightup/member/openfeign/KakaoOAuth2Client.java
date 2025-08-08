package umc.lightup.member.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import umc.lightup.member.dto.OAuth2RequestDTO;
import umc.lightup.member.dto.OAuth2ResponseDTO;

@FeignClient(name = "kakaoOAuth2Client", url = "https://kauth.kakao.com")
public interface KakaoOAuth2Client {
    @PostMapping(value = "/oauth/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<OAuth2ResponseDTO.KakaoOAuth2ResponseDTO> getToken
            (@RequestBody OAuth2RequestDTO.KakaoOAuth2RequestDTO body);
}