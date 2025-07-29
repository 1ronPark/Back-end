package umc.lightup.strength.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.enums.StrengthType;

import java.util.List;

@Repository
public interface StrengthRepository extends JpaRepository<Strength, Long> {
    @Query("select s from Strength s order by case " +
            "s.strengthType " +
            "when :skillType then 1 " +
            "when umc.lightup.strength.enums.StrengthType.COMMON then 2 " +
            "else 3 " +
            "end")
    List<Strength> findAllOrderedByStrengthType(@Param("skillType") StrengthType strengthType);
    //커스텀 강점 생성 기능 삭제
/*    @Query("select s from Strength s where s.isCustom = false")
    List<Strength> findBasicStrengths();
    @Query("select count(*) from Strength where name = :name and isCustom = false")
    Long countByNameAndIsCustomFalse (@Param("name") String name);
    @Query("select s.name from MemberStrength ms join ms.strength s where ms.member=:member")
    List<String> findNameByMember(@Param("member") Member member);*/
}
