package umc.lightup.member.repository;

import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;

import java.util.List;

public interface MemberRepositoryCustom {
    MemberResponseDTO.MemberInfoListDTO getMemberInfos
            (Member requestedMember, MemberRequestDTO.MemberSearchRequestDTO options);
    List<MemberResponseDTO.HistoryInfoDTO> getMemberViewHistoryInfos
            (Member requestedMember, long size);
    MemberResponseDTO.MemberInfoDTO getSingleMemberInfo
            (Member requestedMember, long id);
}
