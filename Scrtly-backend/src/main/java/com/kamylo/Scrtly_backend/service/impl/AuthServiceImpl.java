package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.service.AuthService;
import com.kamylo.Scrtly_backend.service.JwtService;
import com.kamylo.Scrtly_backend.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final Mapper<UserEntity, UserDto> mapper;

    @Override
    public UserDto createUser(RegisterRequestDto registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(email -> {
                    throw new CustomException(BusinessErrorCodes.EMAIL_IS_USED);
                });

        userRepository.findByNickName(registerRequest.getNickName())
                .ifPresent(email -> {
                    throw new CustomException(BusinessErrorCodes.NICKNAME_IS_USED);
                });
        UserEntity user;

        if ("Artist".equalsIgnoreCase(registerRequest.getRole())) {
            user = ArtistEntity.builder()
                    .fullName(registerRequest.getFullName())
                    .nickName(registerRequest.getNickName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .artistName(registerRequest.getArtistName())
                    .build();
        } else {
            user = UserEntity.builder()
                    .fullName(registerRequest.getFullName())
                    .nickName(registerRequest.getNickName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .build();
        }
        UserEntity savedUser = userRepository.save(user);
        return mapper.mapTo(savedUser);
    }

    @Override
    public Map<String, String> verify(LoginRequestDto loginRequest) {
        userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String email = loginRequest.getEmail();
        Map<String, String> objects = new HashMap<>();
        objects.put("jwt", jwtService.generateToken(email));
        objects.put("refresh", refreshTokenService.createRefreshToken(email).getToken());
        return objects;
    }
}
