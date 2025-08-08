package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemCategory;

import java.util.List;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    List<ItemCategory> findByItem(Item item);
    @Query("SELECT ic FROM ItemCategory ic WHERE ic.item IN :items")
    List<ItemCategory> findByItems(@Param("items")List<Item> items);
}
