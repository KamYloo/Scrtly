package com.kamylo.Scrtly_backend.auth.web.controller;

import com.kamylo.Scrtly_backend.auth.service.AuthService;
import com.kamylo.Scrtly_backend.auth.service.CookieService;
import com.kamylo.Scrtly_backend.auth.service.JwtService;
import com.kamylo.Scrtly_backend.auth.service.RefreshTokenService;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RestPasswordRequest;
import com.kamylo.Scrtly_backend.auth.web.dto.response.LoginResponseDto;
import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.auth.domain.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
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
    private final Mapper<UserEntity, UserDto> mapper;

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
        response.addCookie(cookieService.getNewCookie("jwt", tokens.get("jwt"), 2 * 60 * 60));
        response.addCookie(cookieService.getNewCookie("refresh", tokens.get("refresh"), 7 * 24 * 60 * 60));
        UserDto userDto = mapper.mapTo(userService.findUserByEmail(loginRequest.getEmail()));
        LoginResponseDto responseDto = LoginResponseDto.builder()
                .user(userDto)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawRefreshToken = cookieService.getCookieValue(request, "refresh");
        try {
            RefreshTokenEntity token = refreshTokenService.findByToken(rawRefreshToken);
            refreshTokenService.verifyExpiration(token);

            UserEntity user = token.getUser();
            String jwtToken = jwtService.generateToken(user.getEmail());
            RefreshTokenResponse newRefreshToken  = refreshTokenService.createRefreshToken(user.getEmail());
            response.addCookie(cookieService.getNewCookie("jwt", jwtToken, 2 * 60 * 60));
            response.addCookie(cookieService.getNewCookie("refresh", newRefreshToken.getRefreshToken(), 7 * 24 * 60 * 60));
            return new ResponseEntity<>("Tokens refreshed successfully", HttpStatus.OK);
        } catch (CustomException e) {
            response.addCookie(cookieService.deleteCookie("jwt"));
            response.addCookie(cookieService.deleteCookie("refresh"));
            return new ResponseEntity<>("Refresh token expired", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String jwt = cookieService.getCookieValue(request, "jwt");
        String username = jwtService.extractUserName(jwt);
        refreshTokenService.deleteByUserEmail(username);
        response.addCookie(cookieService.deleteCookie("jwt"));
        response.addCookie(cookieService.deleteCookie("refresh"));
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<UserDto> checkAuth(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDto dto = mapper.mapTo(userService.findUserByEmail(ud.getUsername()));
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

    @PostMapping("/reset-password/{userId}/{token}")
    public ResponseEntity<?> reset_password(
            @PathVariable Long userId,
            @PathVariable String token,
            @RequestBody RestPasswordRequest restPasswordRequest
    ) {
        authService.restPassword(userId, token, restPasswordRequest);
        return new ResponseEntity<>("Password has been rested", HttpStatus.OK);
    }
}