package umc.lightup.member.service;

import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;

public interface MemberCommandService {
    Member joinMember(MemberRequestDTO.JoinDto request);
    MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request);
    Member getMember(String email);
    MemberResponseDTO.MemberInfoDTO getMember(long id, String viewerEmail);
    Member putMember(String email, MemberRequestDTO.ChangeDto request);
    String selectSkill(Long skillId,Member member);
    String selectStrength(Long strengthId,Member member);
    void addMemberLike(Member fromMember, long toMemberId);
    void removeMemberLike(String fromMemberEmail, long toMemberId);
    boolean isNicknameExist(String nickname);
    boolean isEmailExist(String email);
    boolean isPhoneNumberExist(String phoneNumber);
    void selectPosition(Long memberId, String positionName);
    void deletePosition(Long memberId, String positionName);
}