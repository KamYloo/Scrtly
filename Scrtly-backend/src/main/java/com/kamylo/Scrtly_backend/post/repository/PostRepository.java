package com.kamylo.Scrtly_backend.post.repository;

import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {
    @EntityGraph(attributePaths = {"user"})
    Page<PostEntity> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable);

    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId")
    void decrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostEntity p SET p.commentCount = p.commentCount + 1 WHERE p.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostEntity p SET p.commentCount = p.commentCount - 1 WHERE p.id = :id")
    void decrementCommentCount(@Param("id") Long id);
}
