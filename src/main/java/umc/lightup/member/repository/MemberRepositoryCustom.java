package umc.lightup.member.repository;

import umc.lightup.member.domain.Member;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;

public interface MemberRepositoryCustom {
    MemberResponseDTO.MemberInfoListDTO getMemberInfos
            (Member requestedMember, MemberRequestDTO.MemberSearchRequestDTO options);
}
