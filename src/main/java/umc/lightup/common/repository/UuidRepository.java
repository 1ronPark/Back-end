package umc.lightup.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.common.Uuid;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
