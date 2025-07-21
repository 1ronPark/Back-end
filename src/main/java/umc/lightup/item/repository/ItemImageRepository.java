package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.ItemImage;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
}
