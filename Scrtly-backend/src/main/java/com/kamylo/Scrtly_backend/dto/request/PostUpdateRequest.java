package com.kamylo.Scrtly_backend.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {
    private Long postId;
    private String description;
}
