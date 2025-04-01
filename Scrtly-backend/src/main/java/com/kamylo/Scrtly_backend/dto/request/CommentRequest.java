package com.kamylo.Scrtly_backend.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequest {
    private Long postId;
    private String comment;
}
