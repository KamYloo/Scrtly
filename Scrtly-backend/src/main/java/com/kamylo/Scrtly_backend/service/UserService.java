package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.UpdateUserRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface UserService {
     User findUserProfileByJwt(String jwt) throws UserException;

     User findUserById(Long userId) throws UserException;

     User followUser(Long userId, User user) throws UserException;

     User updateUser(Long userId, String fullName, String description, MultipartFile userImage) throws UserException;

     Set<User> searchUser(String query);

}