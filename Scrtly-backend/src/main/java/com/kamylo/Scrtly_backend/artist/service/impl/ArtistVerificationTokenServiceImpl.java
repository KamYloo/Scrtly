package com.kamylo.Scrtly_backend.artist.service.impl;

import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.artist.service.ArtistVerificationTokenService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.artist.repository.ArtistVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ArtistVerificationTokenServiceImpl implements ArtistVerificationTokenService {
    private final ArtistVerificationTokenRepository tokenRepository;
    private static final long EXPIRATION_SECONDS = 86400;

    @Override
    public ArtistVerificationToken createArtistVerificationToken(UserEntity user, String requestedArtistName) {
        ArtistVerificationToken verificationToken = ArtistVerificationToken.builder()
                .token(generateVerificationToken(20))
                .user(user)
                .expiryDate(Instant.now().plusSeconds(EXPIRATION_SECONDS))
                .requestedArtistName(requestedArtistName)
                .build();
        return tokenRepository.save(verificationToken);
    }

    @Override
    public ArtistVerificationToken getTokenByUser(UserEntity user) {
        return tokenRepository.findByUser(user)
                .orElse(null);
    }

    @Override
    public boolean tokenExpired(ArtistVerificationToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Override
    public void deleteToken(ArtistVerificationToken token) {
        tokenRepository.delete(token);
    }

    private String generateVerificationToken(int lenght) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < lenght; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }
}
