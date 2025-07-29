package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.config.EmailService;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.dto.EmailRequestDTO;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.repository.CredentialRepository;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialQueryServiceImpl implements CredentialQueryService {
    private final CredentialRepository credentialRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;

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
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));
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
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.MEMBER_NOT_FOUND));

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
}