package com.kamylo.Scrtly_backend.comment.web.controller;

import com.kamylo.Scrtly_backend.comment.web.dto.CommentDto;
import com.kamylo.Scrtly_backend.comment.web.dto.request.CommentRequest;
import com.kamylo.Scrtly_backend.comment.web.dto.request.CommentUpdateRequest;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.comment.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Validated
@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentRequest commentRequest,
                                                    Principal principal) {
        CommentDto comment = commentService.createComment(commentRequest, principal.getName());
        return new  ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping("/all/{postId}")
    public ResponseEntity<PagedResponse<CommentDto>> getComments(@PathVariable @Positive(message = "{comment.postId.positive}") Long postId,
                                                                 @RequestParam(defaultValue = "all") String sortBy,
                                                                 @RequestParam(defaultValue = "0") @Min(0) int page,
                                                                 @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size,
                                                                 Principal principal)  {
        Pageable pageable = PageRequest.of(page, size);
        String username = (principal != null ? principal.getName() : null);
        Page<CommentDto> comments = commentService.getCommentsByPostId(postId, sortBy, pageable, username);
        return new ResponseEntity<>(PagedResponse.of(comments), HttpStatus.OK);
    }

    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<PagedResponse<CommentDto>> getReplies(@PathVariable @Positive(message = "{comment.parentId.positive}") Long parentCommentId,
                                                                @RequestParam(defaultValue = "0") @Min(0) int page,
                                                                @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size,
                                                                Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        String username = (principal != null ? principal.getName() : null);
        Page<CommentDto> replies = commentService.getReplies(parentCommentId, pageable, username);
        return new ResponseEntity<>(PagedResponse.of(replies), HttpStatus.OK);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable @Positive(message = "{comment.id.positive}") Long commentId,
            @Valid @RequestBody CommentUpdateRequest updateRequest,
            Principal principal) {
        CommentDto comment = commentService.updateComment(commentId, updateRequest.getContent(), principal.getName());
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable @Positive(message = "{comment.postId.positive}") Long commentId,
            Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.ok(commentId);
    }
}
