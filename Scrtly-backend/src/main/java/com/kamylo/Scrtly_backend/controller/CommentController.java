package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.CommentDto;
import com.kamylo.Scrtly_backend.dto.request.CommentRequest;
import com.kamylo.Scrtly_backend.response.PagedResponse;
import com.kamylo.Scrtly_backend.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentRequest commentRequest, Principal principal) {
        CommentDto comment = commentService.createComment(commentRequest, principal.getName());
        return new  ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping("/all/{postId}")
    public ResponseEntity<PagedResponse<CommentDto>> getComments(@PathVariable Long postId,
                                                                 @RequestParam(defaultValue = "all") String sortBy,
                                                                 Pageable pageable,
                                                                 Principal principal)  {
        Page<CommentDto> comments = commentService.getCommentsByPostId(postId, sortBy, pageable, principal.getName());
        return new ResponseEntity<>(PagedResponse.of(comments), HttpStatus.OK);
    }

    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<PagedResponse<CommentDto>> getReplies(@PathVariable Long parentCommentId,
                                                                @PageableDefault(size = 10) Pageable pageable,
                                                                Principal principal) {
        PagedResponse<CommentDto> replies = PagedResponse.of(
                commentService.getReplies(parentCommentId, pageable, principal.getName())
        );
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }


    @PutMapping("/update/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId ,@RequestParam String content , Principal principal) {
        CommentDto comment = commentService.updateComment(commentId, content, principal.getName());
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.ok(commentId);
    }
}
