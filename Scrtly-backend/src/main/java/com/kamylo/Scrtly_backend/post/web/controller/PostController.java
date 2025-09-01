package com.kamylo.Scrtly_backend.post.web.controller;

import com.kamylo.Scrtly_backend.post.web.dto.PostDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.post.service.PostService;
import com.kamylo.Scrtly_backend.post.web.dto.request.PostCreateRequest;
import com.kamylo.Scrtly_backend.post.web.dto.request.PostUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> createPost(@Valid @ModelAttribute PostCreateRequest request,
                                              Principal principal) {

        PostDto post = postService.createPost(principal.getName(), request.getDescription(), request.getFile());
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> updatePost(@PathVariable @Positive(message = "{id.positive}") Long postId,
                                              @ModelAttribute PostUpdateRequest request,
                                              Principal principal) {

        PostDto post = postService.updatePost(postId, principal.getName(), request.getFile(), request.getDescription());
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<PostDto>> getAllPosts(
            @RequestParam(value = "minLikes", required = false) Integer minLikes,
            @RequestParam(value = "maxLikes", required = false) Integer maxLikes,
            @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Principal principal) {
        Sort sort = Sort.by(sortDir, "creationDate", "updateDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        String username = (principal != null ? principal.getName() : null);
        Page<PostDto> posts = postService.getPosts(pageable, username, minLikes, maxLikes);
        return new ResponseEntity<>(PagedResponse.of(posts), HttpStatus.OK);
    }

    @GetMapping("/{nickName}/all")
    public ResponseEntity<PagedResponse<PostDto>> getAllPostsByUser(@PathVariable String nickName,
                                                                    @PageableDefault(size = 10, sort = "creationDate",
                                                                    direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto> posts = postService.getPostsByUser(nickName, pageable);
        return new ResponseEntity<>(PagedResponse.of(posts), HttpStatus.OK);
    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") @Positive(message = "{id.positive}") Long postId,
                                        Principal principal)  {
        postService.deletePost(postId, principal.getName());
        return ResponseEntity.ok(postId);
    }
}
