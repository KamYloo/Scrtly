package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.dto.request.UserRequestDto;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{nickName}")
    public ResponseEntity<UserDto> getProfile(@PathVariable("nickName") String nickName, Principal principal) {
        UserDto user = userService.getUserProfile(nickName, principal.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Set<UserDto>> searchUser(@RequestParam("name") String name) {
        Set<UserDto> users = userService.searchUser(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<UserDto> updateUser(@RequestPart("userDetails") UserRequestDto userRequestDto,
                                                     @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
                                                     Principal principal) {

        UserDto user = userService.updateUser(principal.getName(), userRequestDto, profilePicture);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PutMapping("/follow/{userId}")
    public ResponseEntity<UserDto> followUser(@PathVariable Long userId, Principal principal) {
        UserDto user = userService.followUser(userId, principal.getName());
        return ResponseEntity.ok(user);
    }
}
