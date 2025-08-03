package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberStrength;
import umc.lightup.strength.domain.Strength;

import java.util.List;

@Repository
public interface MemberStrengthRepository extends JpaRepository<MemberStrength, Long> {
    @Query("select s.name from MemberStrength ms join ms.strength s where ms.member = :member")
    List<String> findStrengthNameByMember(@Param("member") Member member);
    boolean existsByMemberAndStrength(Member member, Strength skill);
}

