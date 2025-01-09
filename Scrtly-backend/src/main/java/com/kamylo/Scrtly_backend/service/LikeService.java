package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.LikeDto;

public interface LikeService {
    LikeDto likePost(Long postId, String username);

    LikeDto likeComment(Long commentId, String username);

//    public List<LikeEntity> getLikesByPost(Long postId);
//
//    public List<LikeEntity> getLikesByComment(Long commentId);
}
