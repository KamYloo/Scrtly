package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.dto.request.RegisterRequestDto;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface AuthService {
    UserDto createUser(RegisterRequestDto registerRequest) throws MessagingException;
    Map<String, String> verify(LoginRequestDto loginRequest) throws MessagingException;
    void activateUser(Long id, String token) throws MessagingException;
}
