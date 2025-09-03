package com.kamylo.Scrtly_backend.like.web.controller;

import com.kamylo.Scrtly_backend.like.web.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.like.web.dto.SongLikeDto;
import com.kamylo.Scrtly_backend.like.web.dto.CommentStatsDto;
import com.kamylo.Scrtly_backend.like.service.LikeService;
import com.kamylo.Scrtly_backend.like.service.SongLikeService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Validated
@RestController
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final SongLikeService songLikeService;

    @PutMapping("/post/{postId}/like")
    public ResponseEntity<PostStatsDto> likePost (@PathVariable @Positive(message = "{id.positive}") Long postId, Principal principal) {
        PostStatsDto like = likeService.likePost(postId, principal.getName());
        return new ResponseEntity<>(like, HttpStatus.OK);
    }

    @PutMapping("/comment/{commentId}/like")
    public ResponseEntity<CommentStatsDto> likeComment (@PathVariable @Positive(message = "{id.positive}") Long commentId, Principal principal) {
        CommentStatsDto like = likeService.likeComment(commentId, principal.getName());
        return new ResponseEntity<>(like, HttpStatus.OK);
    }

    @PutMapping("/song/{songId}/like")
    public ResponseEntity<SongLikeDto> likeSong(@PathVariable @Positive(message = "{id.positive}") Long songId, Principal principal) {
        SongLikeDto songLike = songLikeService.likeSong(songId, principal.getName());
        return new ResponseEntity<>(songLike, HttpStatus.OK);
    }
}
