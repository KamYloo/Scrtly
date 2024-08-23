package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public List<User> getAllUser();

    public User findUserProfileByJwt(String jwt) throws UserException;

    public User findUserByEmail(String email);

    public User findUserById(Long userId) throws UserException;

    public User updateUser(Long userId, UpdateUserRequest updateUserRequest) throws UserException;

    public List<User> searchUser(String query);

}