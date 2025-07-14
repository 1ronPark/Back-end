package umc.lightup.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.lightup.region.domain.Region;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT DISTINCT r.sido FROM Region r")
    List<String> findDistinctSido();

    List<Region> findBySido(String sido);
}
