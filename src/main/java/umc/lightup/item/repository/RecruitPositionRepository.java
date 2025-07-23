package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.RecruitPosition;

import java.util.List;

@Repository
public interface RecruitPositionRepository extends JpaRepository<RecruitPosition, Long> {
    List<RecruitPosition> findByItem(Item item);
}
