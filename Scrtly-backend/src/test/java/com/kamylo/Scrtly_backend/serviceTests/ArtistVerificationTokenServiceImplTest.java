package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.artist.repository.ArtistVerificationTokenRepository;
import com.kamylo.Scrtly_backend.artist.service.impl.ArtistVerificationTokenServiceImpl;
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
        String expectedArtistName = "Desired Artist";
        ArgumentCaptor<ArtistVerificationToken> tokenCaptor = ArgumentCaptor.forClass(ArtistVerificationToken.class);
        when(tokenRepository.save(any(ArtistVerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        tokenService.createArtistVerificationToken(user, expectedArtistName);

        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        ArtistVerificationToken capturedToken = tokenCaptor.getValue();

        assertEquals(user, capturedToken.getUser());
        assertNotNull(capturedToken.getToken());
        assertEquals(20, capturedToken.getToken().length());
        assertTrue(capturedToken.getToken().matches("\\d{20}"));

        assertEquals(expectedArtistName, capturedToken.getRequestedArtistName());

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
                .requestedArtistName("TestArtist")
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
        assertTrue(expired);
    }

    @Test
    void testTokenExpiredWhenTokenNotExpired() {
        ArtistVerificationToken token = ArtistVerificationToken.builder()
                .expiryDate(Instant.now().plusSeconds(100))
                .build();

        boolean expired = tokenService.tokenExpired(token);
        assertFalse(expired);
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