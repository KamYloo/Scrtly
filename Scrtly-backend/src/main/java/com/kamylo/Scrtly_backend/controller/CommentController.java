package com.kamylo.Scrtly_backend.controller;


import com.kamylo.Scrtly_backend.exception.CommentException;
import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Comment;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.SendCommentRequest;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.CommentService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/create/{postId}")
    public ResponseEntity<Comment> createCommentHandler(@RequestBody SendCommentRequest sendCommentRequest, @RequestHeader("Authorization") String token) throws UserException, PostException {
        User user = userService.findUserProfileByJwt(token);
        sendCommentRequest.setUser(user);
        Comment comment = commentService.createComment(sendCommentRequest);

        return new  ResponseEntity<>(comment, HttpStatus.OK);
    }

    @GetMapping("/get/{postId}")
    public ResponseEntity<List<Comment>> getAllCommentsByPostIdHandler(@PathVariable Long postId, @RequestHeader("Authorization") String token) throws UserException, PostException {
        List<Comment> comments = commentService.getAllCommentsByPostId(postId);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/like/{commentId}")
    public ResponseEntity<Comment> likeCommentHandler(@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws UserException, CommentException {
        User user = userService.findUserProfileByJwt(token);
        Comment comment = commentService.likeComment(commentId, user.getId());

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PutMapping("/unlike/{commentId}")
    public ResponseEntity<Comment> unlikeCommentHandler(@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws UserException, CommentException {
        User user = userService.findUserProfileByJwt(token);
        Comment comment = commentService.unLikeComment(commentId, user.getId());

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ApiResponse> deleteCommentHandler(@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws UserException, CommentException {
        User user = userService.findUserProfileByJwt(token);
        ApiResponse res = new ApiResponse();

        try {
            Comment comment = commentService.findCommentById(commentId);
            if (comment == null) {
                throw new CommentException("Comment not found with id " + commentId);
            }
            commentService.deleteComment(commentId, user.getId());
            res.setMessage("Comment deleted");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (UserException | CommentException e) {
            res.setMessage(e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        }
    }
}
