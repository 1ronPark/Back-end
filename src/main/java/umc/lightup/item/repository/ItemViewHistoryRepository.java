package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemViewHistory;
import umc.lightup.member.domain.Member;

import java.util.Optional;

public interface ItemViewHistoryRepository extends JpaRepository<ItemViewHistory, Long> {
    Optional<ItemViewHistory> findByMemberAndItem(Member memberId, Item itemId);
    int countByItemId(Long itemId);
}
