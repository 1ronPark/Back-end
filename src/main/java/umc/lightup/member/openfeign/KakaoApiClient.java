package umc.lightup.member.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import umc.lightup.member.dto.OAuth2ResponseDTO;

@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {
    @GetMapping("/v2/user/me")
    ResponseEntity<OAuth2ResponseDTO.KakaoUserinfoResponseDTO> getUserInfo
            (@RequestHeader("Authorization") String authorization);
}