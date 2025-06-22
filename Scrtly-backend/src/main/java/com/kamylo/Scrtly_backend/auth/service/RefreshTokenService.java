package com.kamylo.Scrtly_backend.auth.service;

import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.auth.domain.RefreshTokenEntity;

public interface RefreshTokenService {
    RefreshTokenResponse createRefreshToken(String email);
    void deleteByUserEmail(String email);
    RefreshTokenEntity findByToken(String token);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
}
