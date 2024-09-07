package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

@Data
public class LikeDto {
    private Long id;
    private UserDto user;
    private PostDto post;
    private CommentDto comment;
}
