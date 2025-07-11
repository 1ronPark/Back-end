package umc.lightup.member.service;

import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;

public interface MemberCommandService {
    Member joinMember(MemberRequestDTO.JoinDto request);

    MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request);

    Member getMember(String email);

    boolean isNicknameExist(String nickname);
    boolean isEmailExist(String email);
    boolean isPhoneNumberExist(String phoneNumber);
}