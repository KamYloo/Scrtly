package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.post.id = :postId")
    List<Comment> findCommentByPostId(@Param("postId") Long postId);
}
