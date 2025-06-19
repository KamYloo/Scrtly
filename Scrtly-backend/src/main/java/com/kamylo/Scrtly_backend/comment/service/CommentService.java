package com.kamylo.Scrtly_backend.comment.service;

import com.kamylo.Scrtly_backend.comment.web.dto.CommentDto;
import com.kamylo.Scrtly_backend.comment.web.dto.CommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto createComment(CommentRequest commentRequest, String username);
    CommentDto updateComment(Long commentId, String content, String username);
    Page<CommentDto> getCommentsByPostId(Long postId, String sortBy, Pageable pageable, String username);
    Page<CommentDto> getReplies(Long parentCommentId, Pageable pageable, String username);
    void deleteComment(Long commentId, String username);
}
