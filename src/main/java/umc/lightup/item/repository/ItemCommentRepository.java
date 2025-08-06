package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.ItemComment;
import umc.lightup.member.domain.Member;

@Repository
public interface ItemCommentRepository extends JpaRepository<ItemComment, Long> {
    int deleteByCommentMemberAndId(Member member, Long commentId);
    int countByItemId(Long itemId);
}
