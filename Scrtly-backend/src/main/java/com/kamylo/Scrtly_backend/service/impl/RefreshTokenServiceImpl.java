package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.entity.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.repository.RefreshTokenRepository;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;
    private final UserRepository userRepository;

    @Transactional
    public RefreshTokenEntity createRefreshToken(String email){
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                ()->new RuntimeException("User not found"));
        Optional<RefreshTokenEntity> existingToken = refreshTokenRepo.findByUser(userEntity);
        if (existingToken.isPresent()) {
            return existingToken.get();
        }
        RefreshTokenEntity token = RefreshTokenEntity.builder()
                .user(userEntity)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(12000000))
                .build();
        return refreshTokenRepo.save(token);
    }

    @Transactional
    public void deleteByUserEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                ()->new RuntimeException("User not found"));

        refreshTokenRepo.deleteByUser(userEntity);
    }

    public RefreshTokenEntity findByToken(String token){
        return refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.BAD_JWT_TOKEN));
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token){
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new CustomException(BusinessErrorCodes.TOKEN_EXPIRED);
        }
        return token;
    }
}