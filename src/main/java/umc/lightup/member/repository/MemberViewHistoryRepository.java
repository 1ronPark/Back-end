package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberViewHistory;

import java.util.Optional;

@Repository
public interface MemberViewHistoryRepository extends JpaRepository<MemberViewHistory, Long> {
    Optional<MemberViewHistory> findByFromMemberEmailAndToMember(String fromMemberEmail, Member toMember);
    @Modifying
    @Query("UPDATE MemberViewHistory mvh SET mvh.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE mvh.fromMember.id = (SELECT m.id FROM Member m WHERE m.email = :fromMemberEmail) " +
            "AND mvh.toMember = :toMember")
    int updateTimestamp(@Param("fromMemberEmail") String fromMemberEmail, @Param("toMember") Member toMember);
}