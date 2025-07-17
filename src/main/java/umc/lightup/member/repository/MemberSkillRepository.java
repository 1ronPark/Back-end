package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberSkill;
import umc.lightup.skill.domain.Skill;

public interface MemberSkillRepository extends JpaRepository<MemberSkill, Long> {
    boolean existsByMemberAndSkill(Member member, Skill skill);
}
