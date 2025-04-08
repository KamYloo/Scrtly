package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.entity.PasswordResetToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.PasswordResetTokenRepository;
import com.kamylo.Scrtly_backend.service.PasswordResetTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public PasswordResetToken createToken(UserEntity user) {

        PasswordResetToken foundedToken = passwordResetTokenRepository.findByUser(user);
        if (foundedToken != null) {
            passwordResetTokenRepository.delete(foundedToken);
        }

        PasswordResetToken token = PasswordResetToken.builder()
                .token(generateToken(32))
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        return passwordResetTokenRepository.save(token);
    }

    @Override
    public PasswordResetToken getTokenByUser(UserEntity user) {
        return passwordResetTokenRepository.findByUser(user);
    }

    @Override
    public boolean tokenExpired(PasswordResetToken token) {
        return LocalDateTime.now().isAfter(token.getExpiryDate());
    }

    @Override
    public void deleteToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }

    private String generateToken(int lenght) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < lenght; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }
}
