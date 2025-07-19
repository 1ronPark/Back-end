package umc.lightup.strength.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.strength.domain.Strength;

import java.util.List;

@Repository
public interface StrengthRepository extends JpaRepository<Strength, Long> {
    @Query("select s from Strength s where s.isCustom = false")
    List<Strength> findBasicStrengths();
    @Query("select count(*) from Strength where name = :name and isCustom = false")
    Long countByNameAndIsCustomFalse (@Param("name") String name);
    @Query("select s.name from MemberStrength ms join ms.strength s where ms.member=:member")
    List<String> findNameByMember(@Param("member") Member member);
}
