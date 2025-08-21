package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemViewHistory;
import umc.lightup.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemViewHistoryRepository extends JpaRepository<ItemViewHistory, Long> {
    @EntityGraph(attributePaths = "item")
    List<ItemViewHistory> findTop3ByMemberOrderByViewedAtDesc(Member member);
    Optional<ItemViewHistory> findByMemberAndItem(Member memberId, Item itemId);
    int countByItemId(Long itemId);
}
