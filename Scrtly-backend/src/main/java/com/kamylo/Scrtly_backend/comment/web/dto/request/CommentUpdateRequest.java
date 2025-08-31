package com.kamylo.Scrtly_backend.comment.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateRequest {
    @NotBlank(message = "{comment.text.notblank}")
    @Size(max = 1000, message = "{comment.text.size}")
    private String content;
}
