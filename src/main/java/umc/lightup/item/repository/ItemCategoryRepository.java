package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemCategory;

import java.util.List;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    List<ItemCategory> findByItem(Item item);
}
