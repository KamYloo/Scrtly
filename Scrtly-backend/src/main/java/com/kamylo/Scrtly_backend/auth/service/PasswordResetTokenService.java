package com.kamylo.Scrtly_backend.auth.service;

import com.kamylo.Scrtly_backend.auth.domain.PasswordResetToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;

public interface PasswordResetTokenService {
    PasswordResetToken createToken(UserEntity user);
    PasswordResetToken getTokenByUser(UserEntity user);
    boolean tokenExpired(PasswordResetToken token);
    void deleteToken(PasswordResetToken token);
}
