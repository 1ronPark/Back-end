package umc.lightup.member.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import umc.lightup.member.dto.OAuth2RequestDTO;
import umc.lightup.member.dto.OAuth2ResponseDTO;

@FeignClient(name = "googleOAuth2Client", url = "https://oauth2.googleapis.com")
public interface GoogleOAuth2Client {
    @PostMapping(value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<OAuth2ResponseDTO.GoogleOAuth2ResponseDTO> requestAccessToken
            (@RequestBody OAuth2RequestDTO.GoogleOAuth2RequestDTO body);
}