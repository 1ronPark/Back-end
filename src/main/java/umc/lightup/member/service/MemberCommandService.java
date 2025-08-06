package umc.lightup.member.service;

import org.springframework.web.multipart.MultipartFile;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.Portfolio;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;

import java.util.List;

public interface MemberCommandService {
    Member joinMember(MemberRequestDTO.JoinDto request);
    MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.PasswordLoginRequestDTO request);
    Member getMember(String email);
    MemberResponseDTO.MemberInfoDTO getMember(long id, String viewerEmail);
    Member putMember(String email, MemberRequestDTO.ChangeDto request);
    MemberResponseDTO.MyProfileDTO getMemberProfile(Member member);
    MemberResponseDTO.MyProfileDTO putMemberProfile(Member member, MemberRequestDTO.ProfileChangeDto request);
    String saveMemberProfileImage(Member member, MultipartFile profileImage);
    Portfolio savePortfolio(Member member, String name, MultipartFile portfolioFile);
    Portfolio savePortfolio(Member member, String name, String portfolioLink);
    void removePortfolio(String memberEmail, long portFolioId);
    MemberResponseDTO.MemberInfoListDTO searchMember(Member member, MemberRequestDTO.MemberSearchRequestDTO options);
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