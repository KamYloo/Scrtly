package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.entity.ActivationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;

public interface ActivationTokenService {
    ActivationToken createActivationToken(UserEntity user);
    ActivationToken getActivationTokenByUser(UserEntity user);
    void deleteActivationToken(ActivationToken token);
    boolean verifyExpiration(ActivationToken token);
}
