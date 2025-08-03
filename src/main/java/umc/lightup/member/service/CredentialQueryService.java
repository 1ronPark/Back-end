package umc.lightup.member.service;

import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.dto.OAuth2ResponseDTO;

public interface CredentialQueryService {
    Credential findByEmail(String email);
    Credential updatePasswordByEmail(String email, MemberRequestDTO.PasswordChangeRequestDTO request);
    void initializePasswordByEmail(String email);
    MemberResponseDTO.CredentialInfoResultDTO getMemberCredentials(String email);
    OAuth2ResponseDTO.GoogleUserinfoResponseDTO getGoogleUserinfo(String authCode);
    OAuth2ResponseDTO.KakaoUserinfoResponseDTO getKakaoUserinfo(String authCode);
    MemberResponseDTO.LoginResultDTO getLoginResultDTO(Member member);
}
