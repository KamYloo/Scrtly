package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.dto.mapper.UserDtoMapper;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.service.UserService;

import com.kamylo.Scrtly_backend.util.UserUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfileHandler(@RequestHeader("Authorization") String token) throws UserException {
        if (token != null && token.startsWith("Bearer ")) {

            User user = userService.findUserProfileByJwt(token);
            UserDto userDto = UserDtoMapper.toUserDto(user);
            userDto.setReq_user(true);
            return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);

        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserbyId(@PathVariable Long userId, @RequestHeader("Authorization") String token) throws UserException {
        User reqUser = userService.findUserProfileByJwt(token);
        User user = userService.findUserById(userId);
        UserDto userDto = UserDtoMapper.toUserDto(user);
        userDto.setReq_user(UserUtil.isReqUser(reqUser, user));
        userDto.setFollowed(UserUtil.isFollowedByReqUser(reqUser, user));
        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<Set<UserDto>> searchUserHandler(@RequestParam("name") String name) {
        Set<User> users = userService.searchUser(name);
        Set<UserDto> userDtos = UserDtoMapper.toUserDtos(users);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUserHandler( @RequestPart("fullName") String fullName,
                                                          @RequestPart("profilePicture") MultipartFile profilePicture,
                                                          @RequestPart("description") String description,
                                                          @RequestHeader("Authorization") String token) throws UserException, IOException {
        User user = userService.findUserProfileByJwt(token);

        String fileName = UUID.randomUUID().toString() + "_" + profilePicture.getOriginalFilename();
        Path filePath = Paths.get("src/main/resources/static/uploads").resolve(fileName);
        Files.copy(profilePicture.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = "uploads/" + fileName;
        userService.updateUser(user.getId(), fullName, relativePath, description);
        UserDto userDto = UserDtoMapper.toUserDto(user);

        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{userId}/follow")
    public ResponseEntity<UserDto> followUser(@PathVariable Long userId, @RequestHeader("Authorization") String token) throws UserException {
        User reqUser = userService.findUserProfileByJwt(token);
        User user = userService.followUser(userId, reqUser);
        UserDto userDto = UserDtoMapper.toUserDto(user);
        userDto.setFollowed(UserUtil.isFollowedByReqUser(reqUser, user));
        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);

    }
}
