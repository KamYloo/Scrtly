package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {

    @EntityGraph(attributePaths = {"user", "parentComment", "post"})
    Page<CommentEntity> findByParentCommentId(Long parentCommentId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "parentComment", "post"})
    Page<CommentEntity> findAll(Specification<CommentEntity> spec, Pageable pageable);
}
