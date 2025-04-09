package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.entity.ActivationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.ActivationTokenRepository;
import com.kamylo.Scrtly_backend.service.impl.ActivationTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivationTokenServiceImplTest {

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @InjectMocks
    private ActivationTokenServiceImpl activationTokenService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    public void testCreateActivationToken() {
        when(activationTokenRepository.save(any(ActivationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ActivationToken token = activationTokenService.createActivationToken(user);

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertEquals(user, token.getUser());
        assertEquals(20, token.getToken().length());
        assertTrue(token.getToken().matches("\\d{20}"));

        Instant expectedExpiry = Instant.now().plusSeconds(60 * 5);
        assertTrue(token.getExpiryDate().isBefore(expectedExpiry.plusSeconds(2)) &&
                token.getExpiryDate().isAfter(expectedExpiry.minusSeconds(2)));
        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
    }

    @Test
    public void testGetActivationTokenByUser_found() {
        ActivationToken expectedToken = ActivationToken.builder()
                .token("12345678901234567890")
                .user(user)
                .expiryDate(Instant.now().plusSeconds(300))
                .build();
        when(activationTokenRepository.findByUser(user)).thenReturn(Optional.of(expectedToken));

        ActivationToken actualToken = activationTokenService.getActivationTokenByUser(user);

        assertNotNull(actualToken);
        assertEquals(expectedToken.getToken(), actualToken.getToken());
    }

    @Test
    public void testGetActivationTokenByUser_notFound() {
        when(activationTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                activationTokenService.getActivationTokenByUser(user)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testDeleteActivationToken() {
        ActivationToken token = ActivationToken.builder()
                .token("12345678901234567890")
                .user(user)
                .expiryDate(Instant.now().plusSeconds(300))
                .build();

        activationTokenService.deleteActivationToken(token);
        verify(activationTokenRepository, times(1)).delete(token);
    }

    @Test
    public void testVerifyExpiration_expiredToken() {
        ActivationToken token = ActivationToken.builder()
                .token("12345678901234567890")
                .user(user)
                .expiryDate(Instant.now().minusSeconds(10))
                .build();

        boolean isExpired = activationTokenService.verifyExpiration(token);
        assertTrue(isExpired);
    }

    @Test
    public void testVerifyExpiration_notExpiredToken() {
        ActivationToken token = ActivationToken.builder()
                .token("12345678901234567890")
                .user(user)
                .expiryDate(Instant.now().plusSeconds(300))
                .build();

        boolean isExpired = activationTokenService.verifyExpiration(token);
        assertFalse(isExpired);
    }
}