package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.LikeDto;
import com.kamylo.Scrtly_backend.dto.mapper.LikeDtoMapper;
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
    public ResponseEntity<LikeDto> likePost (@PathVariable Long postId, @RequestHeader("Authorization") String token) throws UserException, PostException {
        User user = userService.findUserProfileByJwt(token);
        Like like = likeService.likePost(postId, user);
        LikeDto likeDto = LikeDtoMapper.toLikePostDto(like,user);

        return new ResponseEntity<>(likeDto, HttpStatus.CREATED);
    }

    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment (@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws UserException, CommentException {
        User user = userService.findUserProfileByJwt(token);
        Like like = likeService.likeComment(commentId, user);
        LikeDto likeDto = LikeDtoMapper.toLikeCommentDto(like,user);

        return new ResponseEntity<>(likeDto, HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}/likes")
    public ResponseEntity<List<LikeDto>> getAllPostLikes (@PathVariable Long postId, @RequestHeader("Authorization") String token) throws PostException, UserException {
        User user = userService.findUserProfileByJwt(token);
        List<Like> likes = likeService.getLikesByPost(postId);

        List<LikeDto> likeDtos = LikeDtoMapper.toLikePostList(likes, user);

        return new ResponseEntity<>(likeDtos, HttpStatus.OK);
    }

    @GetMapping("/comment/{commentId}/likes")
    public ResponseEntity<List<LikeDto>> getAllCommentLikes (@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws CommentException, UserException {
        User user = userService.findUserProfileByJwt(token);
        List<Like> likes = likeService.getLikesByComment(commentId);

        List<LikeDto> likeDtos = LikeDtoMapper.toLikeCommentList(likes, user);
        return new ResponseEntity<>(likeDtos, HttpStatus.OK);
    }
}
