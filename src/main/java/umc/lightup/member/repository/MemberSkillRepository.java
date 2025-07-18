package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.MemberSkill;

@Repository
public interface MemberSkillRepository extends JpaRepository<MemberSkill, Long> {
}