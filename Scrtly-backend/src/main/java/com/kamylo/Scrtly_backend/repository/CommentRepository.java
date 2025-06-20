package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.CommentEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {

    @EntityGraph(attributePaths = {"user", "parentComment", "post"})
    Page<CommentEntity> findByParentCommentId(Long parentCommentId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "parentComment", "post"})
    Page<CommentEntity> findAll(Specification<CommentEntity> spec, Pageable pageable);
}
