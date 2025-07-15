package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.MemberPosition;

import java.util.Optional;

@Repository
public interface MemberPositionRepository extends JpaRepository<MemberPosition, Long> {
    Optional<MemberPosition> findByMemberIdAndPositionId(Long memberId, Long positionId);
    boolean existsByMemberIdAndPositionId(Long memberId, Long positionId);
    void deleteByMemberIdAndPositionId(Long memberId, Long positionId);
}
