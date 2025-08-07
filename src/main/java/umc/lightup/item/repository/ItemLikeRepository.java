package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.ItemLike;

@Repository
public interface ItemLikeRepository extends JpaRepository<ItemLike, Long> {
    boolean existsByMemberIdAndItemId(long memberId, long itemId);
    int removeByMemberEmailAndItemId(String memberEmail, long itemId);
}
