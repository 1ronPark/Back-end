package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.MemberSkill;

import java.util.List;

@Repository
public interface MemberSkillRepository extends JpaRepository<MemberSkill, Long> {
    // 이게 성능은 좋지만 설계 관점에서는 SkillRepository에 있어야 하지 않을까...
    @Query("select s.name from MemberSkill ms join ms.skill s where ms.member=:member")
    List<String> findNameByMember(Member member);
}