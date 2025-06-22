package com.kamylo.Scrtly_backend.comment.web.dto;

import com.kamylo.Scrtly_backend.post.web.dto.PostMinimalDto;
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
public class CommentDto {
    private Long id;
    private String comment;
    private LocalDateTime creationDate;
    private UserMinimalDto user;
    private PostMinimalDto post;
    private int likeCount;
    private boolean likedByUser;
    private Long parentCommentId;
}
