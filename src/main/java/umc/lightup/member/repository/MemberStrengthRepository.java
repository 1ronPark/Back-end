package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberStrength;
import umc.lightup.strength.domain.Strength;

public interface MemberStrengthRepository extends JpaRepository<MemberStrength, Long> {
    boolean existsByMemberAndStrength(Member member, Strength skill);
}

