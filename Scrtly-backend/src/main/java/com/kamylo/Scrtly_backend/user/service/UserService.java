package com.kamylo.Scrtly_backend.user.service;

import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.artist.web.dto.request.ArtistVerificationRequest;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.web.dto.request.UserRequestDto;
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
     void requestArtistVerification(String username, ArtistVerificationRequest request);
     boolean isPremium(String email);
}