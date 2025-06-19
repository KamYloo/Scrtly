package com.kamylo.Scrtly_backend.post.web.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {
    private Long postId;
    private String description;
}
