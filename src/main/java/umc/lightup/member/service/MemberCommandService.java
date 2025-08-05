package umc.lightup.member.service;

import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;

import java.util.List;

public interface MemberCommandService {
    Member joinMember(MemberRequestDTO.JoinDto request);
    MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request);
    Member getMember(String email);
    MemberResponseDTO.MemberInfoDTO getMember(long id, String viewerEmail);
    Member putMember(String email, MemberRequestDTO.ChangeDto request);
    String selectSkill(Long skillId,Member member);
    void removeMemberSkill(Long skillId, Long memberId);
    String selectStrength(Long strengthId,Member member);
    void removeMemberStrength(Long strengthId, Long memberId);
    List<MemberResponseDTO.singleRegionResultDTO> selectRegions(Member member, MemberRequestDTO.MemberRegionListRequestDTO request);
    void removeMemberRegion(Long memberRegionId, Long memberId);
    void addMemberLike(Member fromMember, long toMemberId);
    void removeMemberLike(String fromMemberEmail, long toMemberId);
    boolean isNicknameExist(String nickname);
    boolean isEmailExist(String email);
    boolean isPhoneNumberExist(String phoneNumber);
    void selectPosition(Long memberId, String positionName);
    void deletePosition(Long memberId, String positionName);
}