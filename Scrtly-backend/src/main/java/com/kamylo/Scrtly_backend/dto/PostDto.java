package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String image;
    private String description;

    private UserDto user;

    private LocalDateTime creationDate;

    private int totalLikes;

    private int totalComments;

    private boolean isLiked;
}
