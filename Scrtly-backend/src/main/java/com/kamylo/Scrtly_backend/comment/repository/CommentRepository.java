package com.kamylo.Scrtly_backend.comment.repository;

import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {

    @EntityGraph(attributePaths = {"user", "parentComment", "post"})
    Page<CommentEntity> findByParentCommentId(Long parentCommentId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "parentComment", "post"})
    Page<CommentEntity> findAll(Specification<CommentEntity> spec, Pageable pageable);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.likeCount = c.likeCount - 1 WHERE c.id = :id")
    void decrementLikeCount(@Param("id") Long id);
}
