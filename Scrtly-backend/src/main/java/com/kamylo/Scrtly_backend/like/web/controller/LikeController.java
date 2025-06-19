package com.kamylo.Scrtly_backend.like.web.controller;

import com.kamylo.Scrtly_backend.like.web.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.like.web.dto.SongLikeDto;
import com.kamylo.Scrtly_backend.like.web.dto.CommentStatsDto;
import com.kamylo.Scrtly_backend.like.service.LikeService;
import com.kamylo.Scrtly_backend.like.service.SongLikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


@RestController
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final SongLikeService songLikeService;

    @PutMapping("/post/{postId}/like")
    public ResponseEntity<PostStatsDto> likePost (@PathVariable Long postId, Principal principal) {
        PostStatsDto like = likeService.likePost(postId, principal.getName());
        return new ResponseEntity<>(like, HttpStatus.OK);
    }

    @PutMapping("/comment/{commentId}/like")
    public ResponseEntity<CommentStatsDto> likeComment (@PathVariable Long commentId, Principal principal) {
        CommentStatsDto like = likeService.likeComment(commentId, principal.getName());
        return new ResponseEntity<>(like, HttpStatus.OK);
    }

    @PutMapping("/song/{songId}/like")
    public ResponseEntity<SongLikeDto> likeSong(@PathVariable Long songId, Principal principal) {
        SongLikeDto songLike = songLikeService.likeSong(songId, principal.getName());
        return new ResponseEntity<>(songLike, HttpStatus.OK);
    }

    /*@GetMapping("/post/{postId}/likes")
    public ResponseEntity<List<LikeDto>> getAllPostLikes (@PathVariable Long postId, @RequestHeader("Authorization") String token) throws PostException, UserException {
        UserEntity userEntity = userService.findUserProfileByJwt(token);
        List<LikeEntity> likeEntities = likeService.getLikesByPost(postId);

        List<LikeDto> likeDtos = LikeDtoMapper.toLikePostList(likeEntities, userEntity);

        return new ResponseEntity<>(likeDtos, HttpStatus.OK);
    }

    @GetMapping("/comment/{commentId}/likes")
    public ResponseEntity<List<LikeDto>> getAllCommentLikes (@PathVariable Long commentId, @RequestHeader("Authorization") String token) throws CommentException, UserException {
        UserEntity userEntity = userService.findUserProfileByJwt(token);
        List<LikeEntity> likeEntities = likeService.getLikesByComment(commentId);

        List<LikeDto> likeDtos = LikeDtoMapper.toLikeCommentList(likeEntities, userEntity);
        return new ResponseEntity<>(likeDtos, HttpStatus.OK);
    }*/
}
