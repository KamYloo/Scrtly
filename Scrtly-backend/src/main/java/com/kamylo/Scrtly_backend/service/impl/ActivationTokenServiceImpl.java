package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.entity.ActivationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.ActivationTokenRepository;
import com.kamylo.Scrtly_backend.service.ActivationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@AllArgsConstructor
public class ActivationTokenServiceImpl implements ActivationTokenService {
    private final ActivationTokenRepository activationTokenRepository;

    @Override
    public ActivationToken createActivationToken(UserEntity user) {
        ActivationToken activationToken = ActivationToken.builder()
                .token(generateActivationToken(20))
                .expiryDate(Instant.now().plusSeconds(60 * 5))
                .user(user)
                .build();
        return activationTokenRepository.save(activationToken);
    }

    @Override
    public ActivationToken getActivationTokenByUser(UserEntity user) {
        return activationTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteActivationToken(ActivationToken token) {
        activationTokenRepository.delete(token);
    }

    @Override
    public boolean verifyExpiration(ActivationToken token) {
        return token.getExpiryDate().compareTo(Instant.now()) < 0;
    }


    private String generateActivationToken(int lenght) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < lenght; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }
}