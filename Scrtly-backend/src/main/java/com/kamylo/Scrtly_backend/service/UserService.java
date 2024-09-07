package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.UpdateUserRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService {
     List<User> getAllUser();

     User findUserProfileByJwt(String jwt) throws UserException;

     User findUserByEmail(String email);

     User findUserById(Long userId) throws UserException;

     User followUser(Long userId, User user) throws UserException;

     User updateUser(Long userId, String fullName, String profilePicturePath, String description) throws UserException;

     List<User> searchUser(String query);

}