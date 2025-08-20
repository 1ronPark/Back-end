package umc.lightup.item.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.enums.CategoryType;
import umc.lightup.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    List<Item> findByMember(Member member);

    @Query("select distinct i from Item i left join fetch i.itemComments ic left join fetch ic.commentMember where i.id = :itemId")
    Optional<Item> findByIdWithCommentsAndCommentMembers(@Param("itemId") Long itemId);

    //fetch join 하는 것과 안 하는 것이 둘 다 필요해 별도 쿼리로 작성
    @Query("select i from Item i join fetch i.member m where i.id = :itemId")
    Optional<Item> findByIdWithOwner(@Param("itemId") long itemId);

    @Modifying
    @Query("update Item i set i.viewCount = i.viewCount + 1 where i.id = :itemId")
    int increaseViewCount(@Param("itemId") Long itemId);
    int deleteByMemberAndId(Member member, Long itemId);

    Page<Item> findAll(Pageable pageable);

    @Query("select i from Item i join i.itemCategories ic where ic.categoryType = :category")
    Page<Item> findByCategory(@Param("category") CategoryType category, Pageable pageable);

    boolean existsByIdAndMember(long id, Member member);
}
