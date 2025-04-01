package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.CommentDto;
import com.kamylo.Scrtly_backend.dto.request.CommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto createComment(CommentRequest commentRequest, String username);
    CommentDto updateComment(Long commentId, String content, String username);
    Page<CommentDto> getCommentsByPostId(Long postId, String sortBy, Pageable pageable, String username);
    void deleteComment(Long commentId, String username);
}
