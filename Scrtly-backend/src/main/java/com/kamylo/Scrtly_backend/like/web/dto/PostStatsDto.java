package com.kamylo.Scrtly_backend.like.web.dto;

import lombok.Data;

@Data
public class PostStatsDto {
    private Long id;
    private Long postId;
    private int likeCount;
    private int commentCount;
    private boolean likedByUser;
}
