package com.kamylo.Scrtly_backend.common.utils;

import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserLikeChecker {

    public boolean isPostLikedByUser(PostEntity post, Long userId) {
        return post.getLikes() != null && post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userId));
    }

    public boolean isUserFollowed(UserEntity user, Long userId) {
        return user.getFollowers() != null && user.getFollowers().stream()
                .anyMatch(follower -> follower.getId().equals(userId));
    }

    public boolean isCommentLikedByUser(CommentEntity comment, Long userId) {
        return comment.getLikes() != null && comment.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userId));
    }
}

