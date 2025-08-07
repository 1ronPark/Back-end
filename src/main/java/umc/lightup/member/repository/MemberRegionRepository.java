package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberRegion;

import java.util.List;

@Repository
public interface MemberRegionRepository extends JpaRepository<MemberRegion, Long> {
    List<MemberRegion> findByMember(Member member);
    int deleteByIdAndMemberId(Long memberRegionId, Long memberId);
}