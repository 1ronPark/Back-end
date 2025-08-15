package umc.lightup.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.school.domain.SchoolEmailVerification;

import java.util.Optional;

public interface SchoolEmailVerificationRepository extends JpaRepository<SchoolEmailVerification, Long> {
  Optional<SchoolEmailVerification> findByEmail(String email);
}
