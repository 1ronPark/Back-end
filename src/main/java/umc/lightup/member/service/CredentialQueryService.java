package umc.lightup.member.service;

import umc.lightup.member.domain.Credential;
import umc.lightup.member.dto.MemberRequestDTO;

public interface CredentialQueryService {
    Credential findByEmail(String email);
    Credential updatePasswordByEmail(String email, MemberRequestDTO.PasswordChangeRequestDTO request);
    void checkPasswordByEmail(String email, String password);
    void initializePasswordByEmail(String email);
}
