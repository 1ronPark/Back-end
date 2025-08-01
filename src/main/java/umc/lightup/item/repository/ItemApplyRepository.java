package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemApply;
import umc.lightup.member.domain.Member;

public interface ItemApplyRepository extends JpaRepository<ItemApply, Long> {
    boolean existsByMemberAndItem(Member member, Item item);
}
