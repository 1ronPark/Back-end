package umc.lightup.light_talk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.light_talk.domain.CommentLike;

import java.util.Set;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);
    int removeByMemberEmailAndCommentId(String email, Long commentId);
    @Query("select cl.comment.id from CommentLike cl where cl.member.id = :memberId")
    Set<Long> findCommentIdsLikedByMemberId(@Param("memberId") Long memberId);
}
