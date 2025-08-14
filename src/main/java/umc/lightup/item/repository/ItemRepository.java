package umc.lightup.item.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;
import umc.lightup.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByMember(Member member);

    @Query("select distinct i from Item i left join fetch i.itemComments ic left join fetch ic.commentMember where i.id = :itemId")
    Optional<Item> findByIdWithCommentsAndCommentMembers(@Param("itemId") Long itemId);

    @Modifying
    @Query("update Item i set i.viewCount = i.viewCount + 1 where i.id = :itemId")
    int increaseViewCount(@Param("itemId") Long itemId);
    int deleteByMemberAndId(Member member, Long itemId);

    Page<Item> findAll(Pageable pageable);
}
