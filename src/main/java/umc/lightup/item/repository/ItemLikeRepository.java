package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.ItemLike;

import java.util.Set;

@Repository
public interface ItemLikeRepository extends JpaRepository<ItemLike, Long> {
    boolean existsByMemberIdAndItemId(long memberId, long itemId);
    int removeByMemberEmailAndItemId(String memberEmail, long itemId);

    @Query("select il.item.id from ItemLike il where il.member.id = :memberId")
    Set<Long> findItemIdsLikedByMemberId(@Param("memberId") long memberId);
}
