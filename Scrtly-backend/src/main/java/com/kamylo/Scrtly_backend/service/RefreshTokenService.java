package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.entity.RefreshTokenEntity;

public interface RefreshTokenService {
    RefreshTokenResponse createRefreshToken(String email);
    void deleteByUserEmail(String email);
    RefreshTokenEntity findByToken(String token);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
}
