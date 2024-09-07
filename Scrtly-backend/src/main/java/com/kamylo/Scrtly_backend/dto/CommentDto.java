package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String comment;
    private LocalDateTime creationDate;
    private UserDto user;
    private PostDto post;
    private int totalLikes;
    private boolean isLiked;

}
