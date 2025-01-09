package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    @Query("select i from LikeEntity i where i.user.id = :userId and i.post.id = :postId")
    LikeEntity isLikeExistPost(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("select i from LikeEntity i where i.user.id = :userId and i.comment.id = :commentId")
    LikeEntity isLikeExistComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Query("select i from LikeEntity i where i.post.id=:postId")
    List<LikeEntity> findByPostId(@Param("postId") Long postId);

    @Query("select i from LikeEntity i where i.comment.id=:commentId")
    List<LikeEntity> findByCommentId(@Param("commentId") Long commentId);
}
