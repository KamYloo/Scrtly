package com.kamylo.Scrtly_backend.comment.web.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequest {
    private Long postId;
    private String comment;
    private Long parentCommentId;
}
