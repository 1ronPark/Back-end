package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberRegion;
import umc.lightup.region.domain.Region;

import java.util.List;

@Repository
public interface MemberRegionRepository extends JpaRepository<MemberRegion, Long> {
    @Query("select r from MemberRegion mr join mr.region r where mr.member=:member")
    List<Region> findByMember(Member member);
}