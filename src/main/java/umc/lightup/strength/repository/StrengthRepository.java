package umc.lightup.strength.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.strength.domain.Strength;

@Repository
public interface StrengthRepository extends JpaRepository<Strength, Long> {
    boolean existsByName(String name);
}
