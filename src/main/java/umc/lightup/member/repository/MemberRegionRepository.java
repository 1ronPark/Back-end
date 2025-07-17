package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.MemberRegion;

@Repository
public interface MemberRegionRepository extends JpaRepository<MemberRegion, Long> {
}