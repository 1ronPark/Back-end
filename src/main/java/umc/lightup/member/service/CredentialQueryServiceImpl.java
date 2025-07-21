package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.repository.CredentialRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialQueryServiceImpl implements CredentialQueryService {
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

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
}