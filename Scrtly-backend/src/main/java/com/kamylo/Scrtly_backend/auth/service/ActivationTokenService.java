package com.kamylo.Scrtly_backend.auth.service;

import com.kamylo.Scrtly_backend.auth.domain.ActivationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;

public interface ActivationTokenService {
    ActivationToken createActivationToken(UserEntity user);
    ActivationToken getActivationTokenByUser(UserEntity user);
    void deleteActivationToken(ActivationToken token);
    boolean verifyExpiration(ActivationToken token);
}
