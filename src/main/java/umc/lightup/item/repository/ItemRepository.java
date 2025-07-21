package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
