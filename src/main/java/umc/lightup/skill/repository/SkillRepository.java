package umc.lightup.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.skill.domain.Skill;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Query("select s from Skill s where s.isCustom = false")
    List<Skill> findBasicSkills();
    @Query("select count(*) from Skill where name = :name and isCustom = false")
    Long countByNameAndIsCustomFalse (@Param("name") String name);
    @Query("select s.name from MemberSkill ms join ms.skill s where ms.member=:member")
    List<String> findNameByMember(@Param("member") Member member);
}
