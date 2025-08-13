package umc.lightup.lighttalk.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.lighttalk.domain.Post;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"postComments", "postComments.commentMember"})
    Optional<Post> findPostWithCommentsById(@NonNull Long postId);

    @Query("select p from Post p left join fetch p.postMember pm ")
    Page<Post> findAllWithMember(Pageable pageable);

    @Modifying
    @Query("update Post p set p.likes = p.likes + 1 where p.id = :postId")
    int increasePostLike(@Param("postId") Long postId);

    @Modifying
    @Query("update Post p set p.likes = p.likes - 1 where p.id = :postId")
    int decreasePostLike(@Param("postId") Long postId);
}
