package com.kamylo.Scrtly_backend.auth.web.controller;

import com.kamylo.Scrtly_backend.auth.service.AuthService;
import com.kamylo.Scrtly_backend.auth.service.CookieService;
import com.kamylo.Scrtly_backend.auth.service.JwtService;
import com.kamylo.Scrtly_backend.auth.service.RefreshTokenService;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RestPasswordRequest;
import com.kamylo.Scrtly_backend.auth.web.dto.response.LoginResponseDto;
import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.auth.domain.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final CookieService cookieService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @Value("${mailing.frontend.redirect-url}")
    private String redirectUrl;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDto registerRequest) throws MessagingException {
        UserDto createdUser = authService.createUser(registerRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto loginRequest, HttpServletResponse response) throws MessagingException{
        Map<String, String> tokens  = authService.verify(loginRequest);
        response.addCookie(cookieService.getNewCookie("jwt_zuvoria_v1", tokens.get("jwt_zuvoria_v1"), 2 * 60 * 60));
        response.addCookie(cookieService.getNewCookie("refresh_zuvoria_v1", tokens.get("refresh_zuvoria_v1"), 7 * 24 * 60 * 60));
        UserDto userDto = userMapper.toDto(userService.findUserByEmail(loginRequest.getEmail()));
        userDto.setPremium(userService.isPremium(loginRequest.getEmail()));
        LoginResponseDto responseDto = LoginResponseDto.builder()
                .user(userDto)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawRefreshToken = cookieService.getCookieValue(request, "refresh_zuvoria_v1");
        try {
            RefreshTokenEntity token = refreshTokenService.findByToken(rawRefreshToken);
            refreshTokenService.verifyExpiration(token);

            UserEntity user = token.getUser();
            String jwtToken = jwtService.generateToken(user.getEmail());
            RefreshTokenResponse newRefreshToken  = refreshTokenService.createRefreshToken(user.getEmail());
            response.addCookie(cookieService.getNewCookie("jwt_zuvoria_v1", jwtToken, 2 * 60 * 60));
            response.addCookie(cookieService.getNewCookie("refresh_zuvoria_v1", newRefreshToken.getRefreshToken(), 7 * 24 * 60 * 60));
            return new ResponseEntity<>("Tokens refreshed successfully", HttpStatus.OK);
        } catch (CustomException e) {
            response.addCookie(cookieService.deleteCookie("jwt_zuvoria_v1"));
            response.addCookie(cookieService.deleteCookie("refresh_zuvoria_v1"));
            return new ResponseEntity<>("Refresh token expired", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String jwt = cookieService.getCookieValue(request, "jwt_zuvoria_v1");
        String username = jwtService.extractUserName(jwt);
        refreshTokenService.deleteByUserEmail(username);
        response.addCookie(cookieService.deleteCookie("jwt_zuvoria_v1"));
        response.addCookie(cookieService.deleteCookie("refresh_zuvoria_v1"));
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<UserDto> checkAuth(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var email = ud.getUsername();
        var user = userService.findUserByEmail(email);
        var dto = userMapper.toDto(user);
        dto.setPremium(userService.isPremium(email));
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/active/{userId}/{token}")
    public ResponseEntity<?> active_account(@PathVariable Long userId, @PathVariable String token, HttpServletResponse response)
            throws IOException {
        authService.activateUser(userId, token);
        response.sendRedirect(redirectUrl);
        return new ResponseEntity<>("account has been activated", HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) throws MessagingException {
        authService.forgotPassword(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/change-password/{userId}/{token}")
    public ResponseEntity<?> reset_password(
            @PathVariable Long userId,
            @PathVariable String token,
            @RequestBody RestPasswordRequest restPasswordRequest
    ) {
        authService.restPassword(userId, token, restPasswordRequest);
        return new ResponseEntity<>("Password has been rested", HttpStatus.OK);
    }
}