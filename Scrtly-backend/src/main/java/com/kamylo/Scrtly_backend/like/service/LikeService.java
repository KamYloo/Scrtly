package com.kamylo.Scrtly_backend.like.service;

import com.kamylo.Scrtly_backend.like.web.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.like.web.dto.CommentStatsDto;

public interface LikeService {
    PostStatsDto likePost(Long postId, String username);
    CommentStatsDto likeComment(Long commentId, String username);
}
