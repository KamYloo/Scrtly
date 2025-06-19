package com.kamylo.Scrtly_backend.post.web.dto;

import com.kamylo.Scrtly_backend.user.web.dto.UserMinimalDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String image;
    private String description;
    private UserMinimalDto user;
    private LocalDateTime creationDate;
    private int likeCount;
    private int commentCount;
    private boolean likedByUser;
}
