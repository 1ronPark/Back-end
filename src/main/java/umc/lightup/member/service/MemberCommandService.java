package umc.lightup.member.service;

import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;

public interface MemberCommandService {
    Member joinMember(MemberRequestDTO.JoinDto request);
    MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request);
    Member getMember(String email);
    MemberResponseDTO.MemberInfoDTO getMember(long id, String viewerEmail);
  
    boolean isNicknameExist(String nickname);
    boolean isEmailExist(String email);
    boolean isPhoneNumberExist(String phoneNumber);
  
    void selectPosition(Long memberId, String positionName);
    void deletePosition(Long memberId, String positionName);
}