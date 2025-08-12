package umc.lightup.lighttalk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.lighttalk.domain.Comment;
import umc.lightup.member.domain.Member;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    int deleteByCommentMemberAndId(Member member, Long commentId);
    int countByPostId(Long postId);

    @Modifying
    @Query("update Comment c set c.likes = c.likes + 1 where c.id = :commentId")
    int increaseCommentLike(@Param("commentId") Long commentId);

    @Modifying
    @Query("update Comment c set c.likes = c.likes - 1 where c.id = :commentId")
    int decreaseCommentLike(@Param("commentId") Long commentId);
}
