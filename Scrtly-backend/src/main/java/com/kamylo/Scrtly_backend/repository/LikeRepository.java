package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("select i from Like i where i.user.id = :userId and i.post.id = :postId")
    Like isLikeExistPost(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("select i from Like i where i.user.id = :userId and i.comment.id = :commentId")
    Like isLikeExistComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Query("select i from Like i where i.post.id=:postId")
    List<Like> findByPostId(@Param("postId") Long postId);

    @Query("select i from Like i where i.comment.id=:commentId")
    List<Like> findByCommentId(@Param("commentId") Long commentId);
}
