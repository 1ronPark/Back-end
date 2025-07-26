package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberSkill;
import umc.lightup.skill.domain.Skill;

import java.util.List;


@Repository
public interface MemberSkillRepository extends JpaRepository<MemberSkill, Long> {
    List<MemberSkill> findByMember(Member member);
    boolean existsByMemberAndSkill(Member member, Skill skill);
}

