package com.kamylo.Scrtly_backend.artist.service;

import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;

public interface ArtistVerificationTokenService {
    ArtistVerificationToken createArtistVerificationToken(UserEntity user, String requestedArtistName);
    ArtistVerificationToken getTokenByUser(UserEntity user);
    boolean tokenExpired(ArtistVerificationToken token);
    void deleteToken(ArtistVerificationToken token);
}
