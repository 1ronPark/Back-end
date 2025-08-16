package umc.lightup.member.service;

import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.enums.CredentialType;

public interface CredentialQueryService {
    Member joinMember(MemberRequestDTO.JoinDto request);
    MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request);
    MemberResponseDTO.LoginResultDTO loginMemberByGoogle(String authCode, String redirectUrl);
    MemberResponseDTO.LoginResultDTO callbackMemberByGoogle(String authCode, String redirectUrl);
    MemberResponseDTO.LoginResultDTO loginMemberByKakao(String authCode, String redirectUrl);
    MemberResponseDTO.LoginResultDTO callbackMemberByKakao(String authCode, String redirectUrl);
    Member joinMemberByGoogle(String authCode, String redirectUrl);
    Member joinMemberByKakao(String authCode, String redirectUrl);
    Member addGoogleLogin(Member member, String authCode, String redirectUrl);
    Member addKakaoLogin(Member member, String authCode, String redirectUrl);
    Member addPasswordLogin(Member member, String password);
    void removeCredential(Member member, CredentialType credentialType);
    Credential findByEmail(String email);
    Credential updatePasswordByEmail(String email, MemberRequestDTO.PasswordChangeRequestDTO request);
    void checkPasswordByEmail(String email, String password);
    void initializePasswordByEmail(String email);
    MemberResponseDTO.CredentialInfoResultDTO getMemberCredentials(String email);
}