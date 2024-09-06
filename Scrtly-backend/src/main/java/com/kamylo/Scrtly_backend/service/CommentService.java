package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.CommentException;
import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Comment;
import com.kamylo.Scrtly_backend.request.SendCommentRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    Comment createComment(SendCommentRequest sendCommentRequest) throws UserException, PostException;

    Comment findCommentById(Long commentId) throws CommentException;

    List<Comment> getAllCommentsByPostId(Long postId) throws PostException;

    void deleteComment(Long commentId, Long userId) throws CommentException, UserException;
}
