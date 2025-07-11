package umc.lightup.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.enums.CredentialType;
import umc.lightup.member.repository.CredentialRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialQueryServiceImpl {
    private final CredentialRepository credentialRepository;

    public Credential findByEmail(String email) {
        Optional<Credential> credential;
        for(CredentialType credentialType : CredentialType.values()) {
            credential = credentialRepository.findByCredentialTypeAndEmail(credentialType, email);
            if (credential.isPresent()) return credential.get();
        }
        throw new GeneralHandler(ErrorStatus.NO_CREDENTIAL);
    }
}