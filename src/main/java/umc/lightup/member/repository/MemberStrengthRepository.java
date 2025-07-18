package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.MemberStrength;

@Repository
public interface MemberStrengthRepository extends JpaRepository<MemberStrength, Long> {
}