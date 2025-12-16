package com.kamylo.Scrtly_backend.like.repository;

import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByUserIdAndPostId(Long userId, Long postId);

    Optional<LikeEntity> findByUserIdAndCommentId(Long userId, Long commentId);

    @Query("SELECT l.post.id FROM LikeEntity l WHERE l.user.id = :userId AND l.post.id IN :postIds")
    Set<Long> findPostIdsLikedByUser(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    @Query("SELECT l.comment.id FROM LikeEntity l WHERE l.user.id = :userId AND l.comment.id IN :commentIds")
    Set<Long> findCommentIdsLikedByUser(@Param("userId") Long userId, @Param("commentIds") List<Long> commentIds);
}
