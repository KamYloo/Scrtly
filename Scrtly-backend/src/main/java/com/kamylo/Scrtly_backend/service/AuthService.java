package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.dto.request.RegisterRequestDto;

import java.util.Map;

public interface AuthService {
    UserDto createUser(RegisterRequestDto registerRequest);
    Map<String, String> verify(LoginRequestDto loginRequest);
}
