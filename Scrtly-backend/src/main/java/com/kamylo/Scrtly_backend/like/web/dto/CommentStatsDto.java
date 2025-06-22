package com.kamylo.Scrtly_backend.like.web.dto;

import lombok.Data;

@Data
public class CommentStatsDto {
    private Long id;
    private Long commentId;
    private int likeCount;
    private boolean likedByUser;
}