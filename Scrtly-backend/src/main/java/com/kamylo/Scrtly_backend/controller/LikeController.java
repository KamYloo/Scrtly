package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.exception.CommentException;
import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.service.LikeService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @PostMapping("/post/{postId}/like")
    public ResponseEntity<Like> likePost (@PathVariable Long postId, @RequestHeader("Authorization") String token) throws UserException, PostException {
        User user = userService.findUserProfileByJwt(token);
        Like like = likeService.likePost(postId, user);

        return new ResponseEntity<>(like, HttpStatus.CREATED);
    }

    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<Like> likeComment (@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws UserException, CommentException {
        User user = userService.findUserProfileByJwt(token);
        Like like = likeService.likeComment(commentId, user);

        return new ResponseEntity<>(like, HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}/likes")
    public ResponseEntity<List<Like>> getAllPostLikes (@PathVariable Long postId, @RequestHeader("Authorization") String token) throws PostException {
        List<Like> likes = likeService.getLikesByPost(postId);

        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/post/{commentId}/likes")
    public ResponseEntity<List<Like>> getAllCommentLikes (@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws CommentException {
        List<Like> likes = likeService.getLikesByComment(commentId);

        return new ResponseEntity<>(likes, HttpStatus.OK);
    }
}
