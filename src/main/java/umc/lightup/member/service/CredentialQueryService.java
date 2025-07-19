package umc.lightup.member.service;

import org.springframework.transaction.annotation.Transactional;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.dto.MemberRequestDTO;

public interface CredentialQueryService {
    Credential findByEmail(String email);
    Credential updatePasswordByEmail(String email, MemberRequestDTO.PasswordChangeRequestDTO request);
}
