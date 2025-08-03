package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.config.EmailService;
import umc.lightup.config.JwtProperties;
import umc.lightup.config.JwtTokenProvider;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.converter.MemberConverter;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.*;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.openfeign.GoogleApiClient;
import umc.lightup.member.openfeign.GoogleOAuth2Client;
import umc.lightup.member.openfeign.KakaoApiClient;
import umc.lightup.member.openfeign.KakaoOAuth2Client;
import umc.lightup.member.repository.CredentialRepository;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialQueryServiceImpl implements CredentialQueryService {
    private final CredentialRepository credentialRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final JwtTokenProvider jwtTokenProvider;

    private final GoogleOAuth2Client googleOAuth2Client;
    private final GoogleApiClient googleApiClient;
    private final KakaoOAuth2Client kakaoOAuth2Client;
    private final KakaoApiClient kakaoApiClient;


    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Override
    public Credential findByEmail(String email) {
        Optional<Credential> credential;
        for(CredentialType credentialType : CredentialType.values()) {
            credential = credentialRepository.findByCredentialTypeAndEmail(credentialType, email);
            if (credential.isPresent()) return credential.get();
        }
        throw new GeneralHandler(ErrorStatus.NO_CREDENTIAL);
    }

    @Override
    @Transactional
    public Credential updatePasswordByEmail(String email, MemberRequestDTO.PasswordChangeRequestDTO request) {
        Credential target = credentialRepository.findByCredentialTypeAndEmail(CredentialType.PASSWORD, email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPrevPassword(), target.getCredential())) {
            throw new GeneralHandler(ErrorStatus.INVALID_PASSWORD);
        }
        target.setCredential(passwordEncoder.encode(request.getNewPassword()));
        credentialRepository.save(target);
        return target;
    }


    private static final String PASSWORD_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*-_=+;,.<>?";
    private static final int PASSWORD_INITIALIZE_LENGTH = 16;

    /**
     * 이메일을 받아 사용자의 비밀번호를 초기화한 후 메일을 발송함
     * @param email 사용자의 이메일
     */
    @Override
    @Transactional
    public void initializePasswordByEmail(String email) {
        Credential target = credentialRepository.findByCredentialTypeAndEmail(CredentialType.PASSWORD, email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));

        // 무작위 생성
        StringBuilder newPassword = new StringBuilder(PASSWORD_INITIALIZE_LENGTH);
        for (int i = 0; i < PASSWORD_INITIALIZE_LENGTH; i++)
            newPassword.append(PASSWORD_CHARACTERS.charAt(secureRandom.nextInt(PASSWORD_CHARACTERS.length())));
        String newPasswordString = newPassword.toString();

        // 메일 전송
        EmailRequestDTO.PasswordInitializeDTO initializeDTO = EmailRequestDTO.PasswordInitializeDTO.builder()
                .userName(target.getMember().getName())
                .tempPassword(newPasswordString)
                .build();
        emailService.sendEmailTemplate(target.getMember().getEmail(),
                "[Lightup] 비밀번호 재설정 안내",
                "password-reset",
                initializeDTO);

        // 비밀번호 재설정
        target.setCredential(passwordEncoder.encode(newPasswordString));
        credentialRepository.save(target);
    }

    @Override
    public MemberResponseDTO.CredentialInfoResultDTO getMemberCredentials(String email) {
        List<Credential> credentials = credentialRepository.findAllByMemberEmail(email);
        return MemberConverter.toSelectCredentialInfoResultDTO(credentials);
    }

    @Override
    public OAuth2ResponseDTO.GoogleUserinfoResponseDTO getGoogleUserinfo(String authCode) {
        OAuth2RequestDTO.GoogleOAuth2RequestDTO oauth2Request =
                OAuth2RequestDTO.GoogleOAuth2RequestDTO.builder()
                        .code(authCode)
                        .client_id(googleClientId)
                        .client_secret(googleClientSecret)
                        .redirect_uri(googleRedirectUri)
                        .grant_type("authorization_code")
                        .build();
        ResponseEntity<OAuth2ResponseDTO.GoogleOAuth2ResponseDTO> oauth2Response =
                googleOAuth2Client.requestAccessToken(oauth2Request);
        if (!oauth2Response.getStatusCode().is2xxSuccessful() ||
                oauth2Response.getBody() == null ||
                oauth2Response.getBody().getAccess_token() == null)
            throw new GeneralHandler(ErrorStatus.INVALID_AUTH_CODE);
        ResponseEntity<OAuth2ResponseDTO.GoogleUserinfoResponseDTO> userinfo =
                googleApiClient.getUserinfo(JwtProperties.TOKEN_PREFIX + oauth2Response.getBody().getAccess_token());
        if (!userinfo.getStatusCode().is2xxSuccessful() ||
                userinfo.getBody() == null ||
                userinfo.getBody().getEmail() == null)
            throw new GeneralHandler(ErrorStatus.AUTH_NOT_GRANTED);
        return userinfo.getBody();
    }

    @Override
    public OAuth2ResponseDTO.KakaoUserinfoResponseDTO getKakaoUserinfo(String authCode) {
        OAuth2RequestDTO.KakaoOAuth2RequestDTO oauth2Request =
                OAuth2RequestDTO.KakaoOAuth2RequestDTO.builder()
                        .code(authCode)
                        .client_id(kakaoClientId)
                        .client_secret(kakaoClientSecret)
                        .redirect_uri(kakaoRedirectUri)
                        .grant_type("authorization_code")
                        .build();
        ResponseEntity<OAuth2ResponseDTO.KakaoOAuth2ResponseDTO> oauth2Response =
                kakaoOAuth2Client.getToken(oauth2Request);
        if (!oauth2Response.getStatusCode().is2xxSuccessful() ||
                oauth2Response.getBody() == null ||
                oauth2Response.getBody().getAccess_token() == null)
            throw new GeneralHandler(ErrorStatus.INVALID_AUTH_CODE);
        ResponseEntity<OAuth2ResponseDTO.KakaoUserinfoResponseDTO> userinfo =
                kakaoApiClient.getUserInfo(JwtProperties.TOKEN_PREFIX + oauth2Response.getBody().getAccess_token());
        if (!userinfo.getStatusCode().is2xxSuccessful() ||
                userinfo.getBody() == null ||
                userinfo.getBody().getEmail() == null)
            throw new GeneralHandler(ErrorStatus.AUTH_NOT_GRANTED);
        return userinfo.getBody();
    }

    @Override
    public MemberResponseDTO.LoginResultDTO getLoginResultDTO(Member member) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(), null,
                Collections.singleton(() -> member.getRole().name())
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);

        return MemberResponseDTO.loginResultDTOBuilder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .build();
    }
}