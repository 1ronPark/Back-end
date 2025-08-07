package umc.lightup.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.enums.SkillType;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Query("select s from Skill s order by case " +
            "s.skillType " +
            "when :skillType then 1 " +
            "else 2 " +
            "end")
    List<Skill> findAllOrderedBySkillType(@Param("skillType") SkillType skillType);

    //커스텀 스킬 기능 삭제
/*    @Query("select s from Skill s where s.isCustom = false")
    List<Skill> findBasicSkills();
    @Query("select count(*) from Skill where name = :name and isCustom = false")
    Long countByNameAndIsCustomFalse (@Param("name") String name);
    @Query("select s.name from MemberSkill ms join ms.skill s where ms.member=:member")
    List<String> findNameByMember(@Param("member") Member member);
 */
}
