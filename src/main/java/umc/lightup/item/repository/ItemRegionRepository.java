package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemRegion;

import java.util.List;

public interface ItemRegionRepository extends JpaRepository<ItemRegion, Long> {
    List<ItemRegion> findByItem(Item item);
}
