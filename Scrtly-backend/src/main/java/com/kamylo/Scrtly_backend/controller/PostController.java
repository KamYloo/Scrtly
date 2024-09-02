package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createPost(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("description") String description,
                                                  @RequestHeader("Authorization") String token) throws UserException {
        if (file.isEmpty()) {
            throw new RuntimeException("Image file not uploaded.");
        }

        User user = userService.findUserProfileByJwt(token);
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get("src/main/resources/static/uploads/postImages").resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Tworzenie obiektu SendPostRequest
            SendPostRequest sendPostRequest = new SendPostRequest();
            sendPostRequest.setDescription(description);
            sendPostRequest.setImage("/uploads/postImages/" + fileName);
            sendPostRequest.setUser(user);
            postService.createPost(sendPostRequest);

            ApiResponse res = new ApiResponse("Post created successfully", true);
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        } catch (IOException e) {
            throw new RuntimeException("Error saving image file.", e);
        }
    }
}
