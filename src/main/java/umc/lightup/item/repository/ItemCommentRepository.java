package umc.lightup.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.item.domain.ItemComment;

@Repository
public interface ItemCommentRepository extends JpaRepository<ItemComment, Long> {
}
