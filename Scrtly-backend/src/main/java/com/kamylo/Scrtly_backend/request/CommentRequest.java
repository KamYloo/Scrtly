package com.kamylo.Scrtly_backend.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequest {
    private Long postId;
    private String comment;
}
