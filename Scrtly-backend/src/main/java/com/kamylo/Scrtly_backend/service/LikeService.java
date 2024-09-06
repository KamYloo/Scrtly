package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.CommentException;
import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {
    Like likePost(Long postId, User user) throws UserException, PostException;

    Like likeComment(Long commentId, User user) throws UserException, CommentException;

    public List<Like> getLikesByPost(Long postId) throws PostException;

    public List<Like> getLikesByComment(Long commentId) throws CommentException;
}
