package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.dto.CommentStatsDto;

public interface LikeService {
    PostStatsDto likePost(Long postId, String username);

    CommentStatsDto likeComment(Long commentId, String username);

//    public List<LikeEntity> getLikesByPost(Long postId);
//
//    public List<LikeEntity> getLikesByComment(Long commentId);
}
