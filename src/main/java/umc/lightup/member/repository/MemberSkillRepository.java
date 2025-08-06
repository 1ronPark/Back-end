package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberSkill;
import umc.lightup.skill.domain.Skill;

import java.util.List;


@Repository
public interface MemberSkillRepository extends JpaRepository<MemberSkill, Long> {
    @Query("select s.name from MemberSkill ms join ms.skill s where ms.member = :member")
    List<String> findSkillNameByMember(@Param("member") Member member);
    boolean existsByMemberAndSkill(Member member, Skill skill);
    int deleteByMemberIdAndSkillId(Long memberId, Long skillId);
}

