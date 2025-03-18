package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.response.PagedResponse;
import com.kamylo.Scrtly_backend.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@RequestParam("file") MultipartFile file,
                                              @RequestParam("description") String description,
                                              Principal principal) {

        PostDto post = postService.createPost(principal.getName(), description, file);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId,
                                              @RequestParam(required = false) String description,
                                              @RequestParam(required = false) MultipartFile file,
                                              Principal principal) {

        PostDto post = postService.updatePost(postId, principal.getName(), file, description);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<PostDto>> getAllPosts(@PageableDefault(size = 10, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable,
                                                              Principal principal) {
        Page<PostDto> posts = postService.getPosts(pageable, principal.getName());
        return new ResponseEntity<>(PagedResponse.of(posts), HttpStatus.OK);

    }

    @GetMapping("/{nickName}/all")
    public ResponseEntity<PagedResponse<PostDto>> getAllPostsByUser(@PathVariable String nickName,
                                                           @PageableDefault(size = 10, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto> posts = postService.getPostsByUser(nickName, pageable);
        return new ResponseEntity<>(PagedResponse.of(posts), HttpStatus.OK);
    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Principal principal)  {
        postService.deletePost(postId, principal.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
