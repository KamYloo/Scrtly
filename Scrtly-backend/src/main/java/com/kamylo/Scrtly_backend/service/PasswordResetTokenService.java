package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.entity.PasswordResetToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;

public interface PasswordResetTokenService {
    PasswordResetToken createToken(UserEntity user);
    PasswordResetToken getTokenByUser(UserEntity user);
    boolean tokenExpired(PasswordResetToken token);
    void deleteToken(PasswordResetToken token);
}
