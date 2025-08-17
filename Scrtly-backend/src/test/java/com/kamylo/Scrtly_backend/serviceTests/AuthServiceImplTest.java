package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.auth.domain.ActivationToken;
import com.kamylo.Scrtly_backend.auth.domain.PasswordResetToken;
import com.kamylo.Scrtly_backend.auth.service.ActivationTokenService;
import com.kamylo.Scrtly_backend.auth.service.JwtService;
import com.kamylo.Scrtly_backend.auth.service.PasswordResetTokenService;
import com.kamylo.Scrtly_backend.auth.service.RefreshTokenService;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RestPasswordRequest;
import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.email.service.EmailService;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.user.repository.RolesRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.auth.service.impl.AuthServiceImpl;
import com.kamylo.Scrtly_backend.user.domain.RoleEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RolesRepository rolesRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;
    @Mock
    private ActivationTokenService activationTokenService;
    @Mock
    private PasswordResetTokenService passwordResetTokenService;
    @Mock
    private UserMapper mapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDto registerRequest;
    private UserEntity userEntity;
    private ActivationToken activationToken;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequestDto();
        registerRequest.setFullName("Test User");
        registerRequest.setNickName("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        userEntity = UserEntity.builder()
                .id(1L)
                .fullName("Test User")
                .nickName("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .accountLocked(false)
                .enable(false)
                .build();

        activationToken = ActivationToken.builder()
                .id(1L)
                .token("activation-token")
                .user(userEntity)
                .build();
    }

    @Test
    void testCreateUserSuccessful() throws MessagingException {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByNickName(registerRequest.getNickName())).thenReturn(Optional.empty());

        RoleEntity userRole = RoleEntity.builder().name("USER").build();
        when(rolesRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        when(activationTokenService.createActivationToken(userEntity)).thenReturn(activationToken);
        when(activationTokenService.getActivationTokenByUser(userEntity)).thenReturn(activationToken);

        when(mapper.toDto(userEntity)).thenReturn(new UserDto());

        UserDto result = authService.createUser(registerRequest);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(activationTokenService, times(1)).createActivationToken(userEntity);
        verify(activationTokenService, times(1)).getActivationTokenByUser(userEntity);
        verify(emailService, times(1)).sendEmail(eq(userEntity.getEmail()),
                eq(userEntity.getFullName()),
                eq(EmailTemplateName.ACTIVATE_ACCOUNT),
                anyString(),
                eq("Account activation"),
                isNull(),
                isNull());
    }

    @Test
    void testCreateUserEmailAlreadyUsed() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(userEntity));

        CustomException ex = assertThrows(CustomException.class, () -> authService.createUser(registerRequest));
        assertEquals(BusinessErrorCodes.EMAIL_IS_USED, ex.getErrorCode());
    }

    @Test
    void testVerifySuccessful() throws MessagingException {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        userEntity.setEnable(true);
        userEntity.setAccountLocked(false);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userEntity));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(loginRequest.getEmail())).thenReturn("jwt-token");

        RefreshTokenResponse refreshToken = new RefreshTokenResponse();
        refreshToken.setRefreshToken("refresh-token");
        when(refreshTokenService.createRefreshToken(loginRequest.getEmail())).thenReturn(refreshToken);

        Map<String, String> result = authService.verify(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.get("jwt"));
        assertEquals("refresh-token", result.get("refresh"));
    }

    @Test
    void testVerifyDisabledUserThrowsException() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        userEntity.setEnable(false);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userEntity));

        CustomException ex = assertThrows(CustomException.class, () -> authService.verify(loginRequest));
        assertEquals(BusinessErrorCodes.ACCOUNT_DISABLED, ex.getErrorCode());
    }

    @Test
    void testActivateUserSuccessful() {
        userEntity.setEnable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        ActivationToken token = ActivationToken.builder().token("valid-token").user(userEntity).build();
        when(activationTokenService.getActivationTokenByUser(userEntity)).thenReturn(token);
        when(activationTokenService.verifyExpiration(token)).thenReturn(false);
        doNothing().when(activationTokenService).deleteActivationToken(token);

        authService.activateUser(1L, "valid-token");

        assertTrue(userEntity.isEnabled());
        verify(userRepository, times(1)).save(userEntity);
        verify(activationTokenService, times(1)).deleteActivationToken(token);
    }

    @Test
    void testActivateUserInvalidToken() {
        userEntity.setEnable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        ActivationToken token = ActivationToken.builder().token("valid-token").user(userEntity).build();
        when(activationTokenService.getActivationTokenByUser(userEntity)).thenReturn(token);

        CustomException ex = assertThrows(CustomException.class, () -> authService.activateUser(1L, "invalid-token"));
        assertEquals(BusinessErrorCodes.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    void testForgotPasswordSendsEmail() throws MessagingException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .id(1L)
                .token("reset-token")
                .user(userEntity)
                .build();
        when(passwordResetTokenService.createToken(userEntity)).thenReturn(resetToken);
        when(passwordResetTokenService.getTokenByUser(userEntity)).thenReturn(resetToken);

        doNothing().when(emailService).sendEmail(eq(userEntity.getEmail()),
                eq(userEntity.getFullName()),
                eq(EmailTemplateName.RESET_PASSWORD),
                anyString(),
                eq("Reset password"),
                isNull(),
                isNull());

        authService.forgotPassword("test@example.com");

        verify(passwordResetTokenService, times(1)).createToken(userEntity);
        verify(emailService, times(1)).sendEmail(eq(userEntity.getEmail()),
                eq(userEntity.getFullName()),
                eq(EmailTemplateName.RESET_PASSWORD),
                anyString(),
                eq("Reset password"),
                isNull(),
                isNull());
    }

    @Test
    void testRestPasswordSuccessful() {
        Long userId = 1L;
        String tokenStr = "reset-token";
        RestPasswordRequest resetRequest = new RestPasswordRequest();
        resetRequest.setPassword("newPassword");
        resetRequest.setPasswordConfirmation("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        PasswordResetToken resetToken = PasswordResetToken.builder().token(tokenStr).build();
        when(passwordResetTokenService.getTokenByUser(userEntity)).thenReturn(resetToken);
        when(passwordResetTokenService.tokenExpired(resetToken)).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        authService.restPassword(userId, tokenStr, resetRequest);

        assertEquals("encodedNewPassword", userEntity.getPassword());
        verify(userRepository, times(1)).save(userEntity);
        verify(passwordResetTokenService, times(1)).deleteToken(resetToken);
    }

    @Test
    void testRestPasswordPasswordMismatchThrowsException() {
        Long userId = 1L;
        String tokenStr = "reset-token";
        RestPasswordRequest resetRequest = new RestPasswordRequest();
        resetRequest.setPassword("newPassword");
        resetRequest.setPasswordConfirmation("differentPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        CustomException ex = assertThrows(CustomException.class, () -> authService.restPassword(userId, tokenStr, resetRequest));
        assertEquals(BusinessErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH, ex.getErrorCode());
    }

    @Test
    void testRestPasswordInvalidTokenThrowsException() {
        Long userId = 1L;
        String tokenStr = "reset-token";
        RestPasswordRequest resetRequest = new RestPasswordRequest();
        resetRequest.setPassword("newPassword");
        resetRequest.setPasswordConfirmation("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        PasswordResetToken resetToken = PasswordResetToken.builder().token("different-token").build();
        when(passwordResetTokenService.getTokenByUser(userEntity)).thenReturn(resetToken);

        CustomException ex = assertThrows(CustomException.class, () -> authService.restPassword(userId, tokenStr, resetRequest));
        assertEquals(BusinessErrorCodes.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    void testRestPasswordExpiredTokenThrowsException() {
        Long userId = 1L;
        String tokenStr = "reset-token";
        RestPasswordRequest resetRequest = new RestPasswordRequest();
        resetRequest.setPassword("newPassword");
        resetRequest.setPasswordConfirmation("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        PasswordResetToken resetToken = PasswordResetToken.builder().token(tokenStr).build();
        when(passwordResetTokenService.getTokenByUser(userEntity)).thenReturn(resetToken);
        when(passwordResetTokenService.tokenExpired(resetToken)).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> authService.restPassword(userId, tokenStr, resetRequest));
        assertEquals(BusinessErrorCodes.TOKEN_EXPIRED, ex.getErrorCode());
        verify(passwordResetTokenService, times(1)).deleteToken(resetToken);
    }

    @Test
    void testVerifyUserNotFoundThrowsException() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> authService.verify(loginRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testActivateUserUserNotFoundThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> authService.activateUser(99L, "any-token"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testVerifyLockedUserThrowsException() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        userEntity.setEnable(true);
        userEntity.setAccountLocked(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userEntity));

        CustomException ex = assertThrows(CustomException.class, () -> authService.verify(loginRequest));
        assertEquals(BusinessErrorCodes.ACCOUNT_LOCKED, ex.getErrorCode());
    }

    @Test
    void testVerifyNotAuthenticatedThrowsException() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        userEntity.setEnable(true);
        userEntity.setAccountLocked(false);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userEntity));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        CustomException ex = assertThrows(CustomException.class, () -> authService.verify(loginRequest));
        assertEquals(BusinessErrorCodes.BAD_CREDENTIALS, ex.getErrorCode());
    }

    @Test
    void testActivateUserAlreadyEnabledThrowsException() {
        userEntity.setEnable(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        CustomException ex = assertThrows(CustomException.class, () -> authService.activateUser(1L, "any-token"));
        assertEquals(BusinessErrorCodes.USER_IS_ENABLE, ex.getErrorCode());
    }

    @Test
    void testActivateUserExpiredTokenThrowsException() {
        userEntity.setEnable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        ActivationToken token = ActivationToken.builder().token("valid-token").user(userEntity).build();
        when(activationTokenService.getActivationTokenByUser(userEntity)).thenReturn(token);
        when(activationTokenService.verifyExpiration(token)).thenReturn(true);

        doNothing().when(activationTokenService).deleteActivationToken(token);

        CustomException ex = assertThrows(CustomException.class, () -> authService.activateUser(1L, "valid-token"));
        assertEquals(BusinessErrorCodes.TOKEN_EXPIRED, ex.getErrorCode());
    }

    @Test
    void testCreateUserNicknameAlreadyUsed() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByNickName(registerRequest.getNickName()))
                .thenReturn(Optional.of(userEntity));

        CustomException ex = assertThrows(CustomException.class, () -> authService.createUser(registerRequest));
        assertEquals(BusinessErrorCodes.NICKNAME_IS_USED, ex.getErrorCode());
    }

    @Test
    void testCreateUserRoleNotFoundThrowsException() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByNickName(registerRequest.getNickName())).thenReturn(Optional.empty());
        when(rolesRepository.findByName("USER")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.createUser(registerRequest));
        assertEquals("Role not found", ex.getMessage());
    }
}
