package umc.lightup.school.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.school.domain.School;

public interface SchoolRepository extends JpaRepository<School, Long> {
  Page<School> findAllByNameContaining(String keyword, PageRequest pageRequest);
}
