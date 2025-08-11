package umc.lightup.light_talk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.light_talk.domain.PostLike;

import java.util.Set;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    int removeByMemberEmailAndPostId(String email, Long postId);
    @Query("select pl.post.id from PostLike pl where pl.member.id = :memberId")
    Set<Long> findPostIdsLikedByMemberId(@Param("memberId") Long memberId);
}
