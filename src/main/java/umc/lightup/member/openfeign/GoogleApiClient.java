package umc.lightup.member.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import umc.lightup.member.dto.OAuth2ResponseDTO;

@FeignClient(name = "googleApiClient", url = "https://www.googleapis.com")
public interface GoogleApiClient {
    @GetMapping(value = "/oauth2/v3/userinfo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<OAuth2ResponseDTO.GoogleUserinfoResponseDTO> getUserinfo
            (@RequestHeader("Authorization") String authHeader);
}