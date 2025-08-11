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
import umc.lightup.member.enums.Role;
import umc.lightup.member.openfeign.GoogleApiClient;
import umc.lightup.member.openfeign.GoogleOAuth2Client;
import umc.lightup.member.openfeign.KakaoApiClient;
import umc.lightup.member.openfeign.KakaoOAuth2Client;
import umc.lightup.member.repository.CredentialRepository;
import umc.lightup.member.repository.MemberRepository;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialQueryServiceImpl implements CredentialQueryService {
    private final MemberRepository memberRepository;
    private final CredentialRepository credentialRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberCommandService memberCommandService;

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
    @Transactional
    public Member joinMember(MemberRequestDTO.JoinDto request) {
        Credential credential = request.toCredential(passwordEncoder);
        Member member = credential.getMember();
        Member saved = memberRepository.save(member);
        credentialRepository.save(credential);
        return saved;
    }

    @Override
    public MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request) {
        Credential credential = credentialRepository
                .findByCredentialTypeAndEmail(CredentialType.PASSWORD, request.getEmail())
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), credential.getCredential())) {
            throw new GeneralHandler(ErrorStatus.INVALID_PASSWORD);
        }

        //주객전도가 되긴 했으나 한 번의 쿼리로 데이터를 가져오기 위한 가장 편한 방법임
        return getLoginResultDTO(credential.getMember());
    }

    @Override
    public MemberResponseDTO.LoginResultDTO loginMemberByGoogle(String authCode) {
        OAuth2ResponseDTO.GoogleUserinfoResponseDTO userinfo = getGoogleUserinfo(authCode);

        Credential credential = credentialRepository
                .findByCredentialTypeAndCredential(CredentialType.GOOGLE, userinfo.getSub())
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));

        return getLoginResultDTO(credential.getMember());
    }

    @Override
    public MemberResponseDTO.LoginResultDTO callbackMemberByGoogle(String authCode) {
        OAuth2ResponseDTO.GoogleUserinfoResponseDTO userinfo = getGoogleUserinfo(authCode);


        Optional<Credential> optionalCredential = credentialRepository
                .findByCredentialTypeAndCredential(CredentialType.GOOGLE, userinfo.getSub());

        if (optionalCredential.isPresent())
            return getLoginResultDTO(optionalCredential.get().getMember());
        else return getLoginResultDTO(joinMemberByGoogle(userinfo));

    }

    @Override
    public MemberResponseDTO.LoginResultDTO loginMemberByKakao(String authCode) {
        OAuth2ResponseDTO.KakaoUserinfoResponseDTO userinfo = getKakaoUserinfo(authCode);

        Credential credential = credentialRepository
                .findByCredentialTypeAndCredential(CredentialType.KAKAO, userinfo.getSub())
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND));

        return getLoginResultDTO(credential.getMember());
    }

    @Override
    public MemberResponseDTO.LoginResultDTO callbackMemberByKakao(String authCode) {
        OAuth2ResponseDTO.KakaoUserinfoResponseDTO userinfo = getKakaoUserinfo(authCode);

        Optional<Credential> optionalCredential = credentialRepository
                .findByCredentialTypeAndCredential(CredentialType.KAKAO, userinfo.getSub());
        if (optionalCredential.isPresent())
            return getLoginResultDTO(optionalCredential.get().getMember());
        else return getLoginResultDTO(joinMemberByKakao(authCode));
    }

    @Override
    @Transactional
    public Member joinMemberByGoogle(String authCode) {
        return joinMemberByGoogle(getGoogleUserinfo(authCode));
    }

    private Member joinMemberByGoogle(OAuth2ResponseDTO.GoogleUserinfoResponseDTO userinfo) {
        if (memberCommandService.isEmailExist(userinfo.getEmail()))
            throw new GeneralHandler(ErrorStatus.ALREADY_SIGNED_IN_EMAIL);

        Member member = Member.builder()
                .email(userinfo.getEmail())
                .name(userinfo.getName())
                .role(Role.PROVISION)
                .build();
        Credential credential = Credential.builder()
                .credentialType(CredentialType.GOOGLE)
                .member(member)
                .credential(userinfo.getSub())
                .build();
        Member saved = memberRepository.save(member);
        credentialRepository.save(credential);
        return saved;
    }

    @Override
    @Transactional
    public Member joinMemberByKakao(String authCode) {
        return joinMemberByKakao(getKakaoUserinfo(authCode));
    }

    private Member joinMemberByKakao(OAuth2ResponseDTO.KakaoUserinfoResponseDTO userinfo) {
        if (userinfo.getEmail() == null || memberCommandService.isEmailExist(userinfo.getEmail()))
            throw new GeneralHandler(ErrorStatus.ALREADY_SIGNED_IN_EMAIL);

        if (memberCommandService.isNicknameExist(userinfo.getNickname()))
            userinfo.setNickname(null);

        Member member = Member.builder()
                .email(userinfo.getEmail())
                .nickname(userinfo.getNickname())
                .profileImageUrl(userinfo.getPicture())
                .role(Role.PROVISION)
                .build();
        Credential credential = Credential.builder()
                .credentialType(CredentialType.KAKAO)
                .member(member)
                .credential(userinfo.getSub())
                .build();
        Member saved = memberRepository.save(member);
        credentialRepository.save(credential);
        return saved;
    }

    @Override
    @Transactional
    public Member addGoogleLogin(Member member, String authCode) {
        if (credentialRepository.existsByCredentialTypeAndMember(CredentialType.GOOGLE, member))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_EXIST);
        OAuth2ResponseDTO.GoogleUserinfoResponseDTO userinfo = getGoogleUserinfo(authCode);
        if (credentialRepository.existsByCredentialTypeAndCredential(CredentialType.GOOGLE, userinfo.getSub()))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_USED);

        Credential credential = Credential.builder()
                .credentialType(CredentialType.GOOGLE)
                .member(member)
                .credential(userinfo.getSub())
                .build();
        credentialRepository.save(credential);
        return member;
    }

    @Override
    @Transactional
    public Member addKakaoLogin(Member member, String authCode) {
        if (credentialRepository.existsByCredentialTypeAndMember(CredentialType.KAKAO, member))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_EXIST);
        OAuth2ResponseDTO.KakaoUserinfoResponseDTO userinfo = getKakaoUserinfo(authCode);
        if (credentialRepository.existsByCredentialTypeAndCredential(CredentialType.KAKAO, userinfo.getSub()))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_USED);

        Credential credential = Credential.builder()
                .credentialType(CredentialType.KAKAO)
                .member(member)
                .credential(userinfo.getSub())
                .build();
        credentialRepository.save(credential);
        return member;
    }

    @Override
    @Transactional
    public Member addPasswordLogin(Member member, String password) {
        if (credentialRepository.existsByCredentialTypeAndMember(CredentialType.PASSWORD, member))
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_ALREADY_EXIST);
        Credential credential = Credential.builder()
                .credentialType(CredentialType.PASSWORD)
                .member(member)
                .credential(passwordEncoder.encode(password))
                .build();
        credentialRepository.save(credential);
        return member;
    }

    @Override
    @Transactional
    public void removeCredential(Member member, CredentialType credentialType) {
        if (credentialRepository.countByMember(member) <= 1)
            throw new GeneralHandler(ErrorStatus.ONLY_CREDENTIAL_REMAIN);

        if (credentialRepository.removeByCredentialTypeAndMember(credentialType, member) == 0)
            throw new GeneralHandler(ErrorStatus.CREDENTIAL_NOT_FOUND);
    }

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

    @Override
    public void checkPasswordByEmail(String email, String password) {
        Credential target = credentialRepository.findByCredentialTypeAndEmail(CredentialType.PASSWORD, email)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(password, target.getCredential())) {
            throw new GeneralHandler(ErrorStatus.INVALID_PASSWORD);
        }
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

    private OAuth2ResponseDTO.GoogleUserinfoResponseDTO getGoogleUserinfo(String authCode) {
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

    private OAuth2ResponseDTO.KakaoUserinfoResponseDTO getKakaoUserinfo(String authCode) {
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

    private MemberResponseDTO.LoginResultDTO getLoginResultDTO(Member member) {
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