package com.kamylo.Scrtly_backend.auth.service;

import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RestPasswordRequest;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface AuthService {
    UserDto createUser(RegisterRequestDto registerRequest) throws MessagingException;
    Map<String, String> verify(LoginRequestDto loginRequest) throws MessagingException;
    void activateUser(Long id, String token);
    void forgotPassword(String email) throws MessagingException;
    void restPassword(Long userId, String token, RestPasswordRequest restPasswordRequest);
}
