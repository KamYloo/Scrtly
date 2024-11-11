package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.dto.mapper.PostDtoMapper;
import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.request.SendPostRequest;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.PostService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@RequestParam("file") MultipartFile file,
                                              @RequestParam("description") String description,
                                              @RequestHeader("Authorization") String token) throws UserException {
        if (file.isEmpty()) {
            throw new RuntimeException("Image file not uploaded.");
        }

        User user = userService.findUserProfileByJwt(token);

        SendPostRequest sendPostRequest = new SendPostRequest();
        sendPostRequest.setDescription(description);
        sendPostRequest.setUser(user);
        Post post = postService.createPost(sendPostRequest, file);

        PostDto postDto = PostDtoMapper.toPostDto(post, user);
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId,
                                              @RequestParam(required = false) String description,
                                              @RequestParam(required = false) MultipartFile file,
                                              @RequestHeader("Authorization") String token) throws UserException, PostException {
        User user = userService.findUserProfileByJwt(token);
        Post updatePost = postService.updatePost(postId,description,file,user.getId());
        PostDto postDto = PostDtoMapper.toPostDto(updatePost, user);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PostDto>> getAllPosts(@RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        List<Post> posts = postService.getAllPosts();
        List<PostDto> postDtos = PostDtoMapper.toPostDtoList(posts, user);
        return new ResponseEntity<>(postDtos, HttpStatus.OK);

    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<PostDto>> getAllPostsByUserHandler(@PathVariable Long userId, @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        List<Post> posts = postService.getAllPostsByUser(userId);
        List<PostDto> postDtos = PostDtoMapper.toPostDtoList(posts, user);
        return new ResponseEntity<>(postDtos, HttpStatus.OK);

    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId, @RequestHeader("Authorization") String token) throws UserException, PostException {
        User user = userService.findUserProfileByJwt(token);
        ApiResponse res = new ApiResponse();

        try {
            postService.deletePost(postId, user.getId());
            res.setMessage("Post  deleted successfully.");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (UserException | PostException e) {
            res.setMessage(e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        }
    }
}
