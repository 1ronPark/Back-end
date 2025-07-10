package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.config.JwtTokenProvider;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.repository.CredentialRepository;
import umc.lightup.member.repository.MemberRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberRepository memberRepository;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


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
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Member member = credential.getMember(); //주객전도가 되긴 했으나 한 번의 쿼리로 데이터를 가져오기 위한 가장 편한 방법임

        if(!passwordEncoder.matches(request.getPassword(), credential.getCredential())) {
            throw new GeneralHandler(ErrorStatus.INVALID_PASSWORD);
        }

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

    @Override
    public Member getMember(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(()-> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public boolean isNicknameExist(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public boolean isEmailExist(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneNumberExist(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }
}