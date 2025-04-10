package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.entity.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;

public interface ArtistVerificationTokenService {
    ArtistVerificationToken createArtistVerificationToken(UserEntity user);
    ArtistVerificationToken getTokenByUser(UserEntity user);
    boolean tokenExpired(ArtistVerificationToken token);
    void deleteToken(ArtistVerificationToken token);
}
