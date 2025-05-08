package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

@Data
public class CommentStatsDto {
    private Long id;
    private Long commentId;
    private int likeCount;
    private boolean likedByUser;
}