package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {

    @Query("select c from CommentEntity c where c.post.id = :postId")
    List<CommentEntity> findCommentByPostId(@Param("postId") Long postId);
}
