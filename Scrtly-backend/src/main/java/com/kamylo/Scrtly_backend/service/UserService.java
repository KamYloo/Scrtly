package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    public List<User> getAllUser() {
        return null;
    }

    public User findUserProfileByJwt(String jwt) {
        return null;
    }

    public User findUserByEmail(String email) {
        return null;
    }

    public User findUserById(String userId) {
        return null;
    }

    public List<User> findAllUsers() {
        return null;
    }

    public User updateUser(Integer userId, UpdateUserRequest updateUserRequest) throws UserException {
        return null;
    }

}