package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.Item;
import umc.lightup.item.domain.ItemApply;
import umc.lightup.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository //잠깐만 이걸 지금 추가했다는 건 이전에는 이게 없었어도 돌아갔다는 건가? 왜 돌아갔지?????
public interface ItemApplyRepository extends JpaRepository<ItemApply, Long> {
    boolean existsByMemberAndItem(Member member, Item item);
    Optional<ItemApply> findByMemberAndItem(Member member, Item item);

    @EntityGraph(attributePaths = {"member", "item", "item.member"})
    Optional<ItemApply> findById(long id); //아니 long을 소문자로 쓰면 Override가 아니네
    @Query("select ia " + //Username만 아니었어도 여기서 사영(select)을 진행했을 듯, 지금은 하기 귀찮음
            "from ItemApply ia join fetch ia.member m join fetch ia.item i join fetch i.member im " +
            "where m.email = :memberEmail or im.email = :memberEmail")
    List<ItemApply> findAllByMemberEmail(String memberEmail);
}
