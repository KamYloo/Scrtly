package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.entity.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.ArtistVerificationTokenRepository;
import com.kamylo.Scrtly_backend.service.impl.ArtistVerificationTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistVerificationTokenServiceImplTest {

    @Mock
    private ArtistVerificationTokenRepository tokenRepository;

    @InjectMocks
    private ArtistVerificationTokenServiceImpl tokenService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(1L)
                .build();
    }

    @Test
    void testCreateArtistVerificationToken() {
        ArgumentCaptor<ArtistVerificationToken> tokenCaptor = ArgumentCaptor.forClass(ArtistVerificationToken.class);
        when(tokenRepository.save(any(ArtistVerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        tokenService.createArtistVerificationToken(user);

        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        ArtistVerificationToken capturedToken = tokenCaptor.getValue();

        assertEquals(user, capturedToken.getUser());
        assertNotNull(capturedToken.getToken());
        assertEquals(20, capturedToken.getToken().length());
        assertTrue(capturedToken.getToken().matches("\\d{20}"));

        Instant now = Instant.now();
        long diffSeconds = capturedToken.getExpiryDate().getEpochSecond() - now.getEpochSecond();
        assertTrue(diffSeconds <= 86400 && diffSeconds >= 86390);
    }

    @Test
    void testGetTokenByUserWhenPresent() {
        ArtistVerificationToken token = ArtistVerificationToken.builder()
                .token("12345678901234567890")
                .user(user)
                .expiryDate(Instant.now().plusSeconds(86400))
                .build();
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(token));

        ArtistVerificationToken retrievedToken = tokenService.getTokenByUser(user);

        assertNotNull(retrievedToken);
        assertEquals("12345678901234567890", retrievedToken.getToken());
    }

    @Test
    void testGetTokenByUserWhenNotPresent() {
        when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

        ArtistVerificationToken retrievedToken = tokenService.getTokenByUser(user);

        assertNull(retrievedToken);
    }

    @Test
    void testTokenExpiredWhenTokenExpired() {
        ArtistVerificationToken token = ArtistVerificationToken.builder()
                .expiryDate(Instant.now().minusSeconds(10))
                .build();

        boolean expired = tokenService.tokenExpired(token);
        assertTrue(expired, "Token powinien być oznaczony jako wygasły.");
    }

    @Test
    void testTokenExpiredWhenTokenNotExpired() {
        ArtistVerificationToken token = ArtistVerificationToken.builder()
                .expiryDate(Instant.now().plusSeconds(100))
                .build();

        boolean expired = tokenService.tokenExpired(token);
        assertFalse(expired, "Token nie powinien być oznaczony jako wygasły.");
    }

    @Test
    void testDeleteToken() {
        ArtistVerificationToken token = ArtistVerificationToken.builder()
                .token("1234567890")
                .build();

        tokenService.deleteToken(token);

        verify(tokenRepository, times(1)).delete(token);
    }
}