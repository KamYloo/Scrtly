package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.UpdateUserRequest;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(@RequestHeader("Authorization") String token) throws UserException {
        if (token != null && token.startsWith("Bearer ")) {

            User user = userService.findUserProfileByJwt(token);
            return new ResponseEntity<>(user, HttpStatus.ACCEPTED);

        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUserHandler(@RequestParam("name") String name) {
        List<User> users = userService.searchUser(name);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUserHandler( @RequestPart("fullName") String fullName,
                                                          @RequestPart("profilePicture") MultipartFile profilePicture,
                                                          @RequestPart("description") String description,
                                                          @RequestHeader("Authorization") String token) throws UserException, IOException {
        User user = userService.findUserProfileByJwt(token);

        String fileName = UUID.randomUUID().toString() + "_" + profilePicture.getOriginalFilename();
        Path filePath = Paths.get("src/main/resources/static/uploads").resolve(fileName);
        Files.copy(profilePicture.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = "uploads/" + fileName;
        userService.updateUser(user.getId(), fullName, relativePath, description);

        ApiResponse res = new ApiResponse("user updated successfully", true);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

}
