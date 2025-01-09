package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.request.UserRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


public interface UserService {
     UserEntity findUserByEmail(String email);
     UserEntity findUserById(Long userId);
     UserDto findUserByNickname(String nickname);
     UserDto getUserProfile(String nickname, String reqUsername);
     UserDto followUser(Long userId, String username);
     UserDto updateUser(String username, UserRequestDto userRequestDto, MultipartFile userImage);
     Set<UserDto> searchUser(String query);
}