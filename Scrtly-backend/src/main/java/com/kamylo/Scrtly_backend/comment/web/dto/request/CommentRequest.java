package com.kamylo.Scrtly_backend.comment.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequest {
    @NotNull(message = "{comment.postId.notnull}")
    @Positive(message = "{comment.postId.positive}")
    private Long postId;

    @NotBlank(message = "{comment.text.notblank}")
    @Size(max = 1000, message = "{comment.text.size}")
    private String comment;

    @Positive(message = "{comment.parentId.positive}")
    private Long parentCommentId; // optional: can be null
}
