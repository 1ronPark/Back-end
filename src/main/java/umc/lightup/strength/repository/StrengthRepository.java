package umc.lightup.strength.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.strength.domain.Strength;

import java.util.List;

@Repository
public interface StrengthRepository extends JpaRepository<Strength, Long> {
//    @Query("select s.name from Strength s where s.id in :ids")
//    List<String> findAllNameById(@Param("ids") List<Long> ids);
}