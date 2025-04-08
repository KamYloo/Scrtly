package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.entity.PasswordResetToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.PasswordResetTokenRepository;
import com.kamylo.Scrtly_backend.service.impl.PasswordResetTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordResetTokenServiceImplTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private PasswordResetTokenServiceImpl passwordResetTokenService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void testCreateTokenWhenNoExistingToken() {
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(null);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> {
            PasswordResetToken token = invocation.getArgument(0);
            token.setId(1L);
            return token;
        });

        PasswordResetToken token = passwordResetTokenService.createToken(user);

        verify(passwordResetTokenRepository).findByUser(user);
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getToken()).hasSize(32);
        assertThat(token.getExpiryDate()).isAfter(LocalDateTime.now().plusMinutes(9));
    }

    @Test
    void testCreateTokenWhenExistingTokenExists() {
        PasswordResetToken existingToken = PasswordResetToken.builder()
                .id(1L)
                .token("12345678901234567890123456789012")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        when(passwordResetTokenRepository.findByUser(user)).thenReturn(existingToken);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> {
            PasswordResetToken token = invocation.getArgument(0);
            token.setId(2L);
            return token;
        });

        PasswordResetToken newToken = passwordResetTokenService.createToken(user);

        verify(passwordResetTokenRepository).delete(existingToken);
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        assertNotEquals(existingToken.getToken(), newToken.getToken());
        assertThat(newToken.getUser()).isEqualTo(user);
    }

    @Test
    void testTokenNotExpired() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("12345678901234567890123456789012")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();

        boolean expired = passwordResetTokenService.tokenExpired(token);

        assertFalse(expired, "Token powinien być ważny (nie wygasł)");
    }

    @Test
    void testTokenExpired() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("12345678901234567890123456789012")
                .user(user)
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .build();

        boolean expired = passwordResetTokenService.tokenExpired(token);

        assertTrue(expired, "Token powinien być uznany za wygasły");
    }

    @Test
    void testGetTokenByUser() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L)
                .token("12345678901234567890123456789012")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();

        when(passwordResetTokenRepository.findByUser(user)).thenReturn(token);

        PasswordResetToken fetchedToken = passwordResetTokenService.getTokenByUser(user);

        verify(passwordResetTokenRepository).findByUser(user);
        assertThat(fetchedToken).isEqualTo(token);
    }

    @Test
    void testDeleteToken() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L)
                .token("12345678901234567890123456789012")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();

        passwordResetTokenService.deleteToken(token);

        verify(passwordResetTokenRepository).delete(token);
    }
}