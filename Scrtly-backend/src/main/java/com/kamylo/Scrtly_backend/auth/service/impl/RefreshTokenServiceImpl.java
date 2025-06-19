package com.kamylo.Scrtly_backend.auth.service.impl;

import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.auth.domain.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.auth.repository.RefreshTokenRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.auth.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RefreshTokenResponse createRefreshToken(String email){
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<RefreshTokenEntity> existingTokenOpt = refreshTokenRepo.findByUser(userEntity);
        String tokenId = UUID.randomUUID().toString();
        String rawTokenPart = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawTokenPart);
        Instant expiryDate = Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000);

        RefreshTokenEntity tokenEntity;
        if(existingTokenOpt.isPresent()){
            tokenEntity = existingTokenOpt.get();
            tokenEntity.setTokenId(tokenId);
            tokenEntity.setToken(hashedToken);
            tokenEntity.setExpiryDate(expiryDate);
        } else {
            tokenEntity = RefreshTokenEntity.builder()
                    .user(userEntity)
                    .tokenId(tokenId)
                    .token(hashedToken)
                    .expiryDate(expiryDate)
                    .build();
        }
        refreshTokenRepo.save(tokenEntity);

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setRefreshToken(tokenId + "." + rawTokenPart);
        return response;
    }


    @Transactional
    public void deleteByUserEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                ()->new RuntimeException("User not found"));

        refreshTokenRepo.deleteByUser(userEntity);
    }

    public RefreshTokenEntity findByToken(String rawToken){
        String decodedToken = URLDecoder.decode(rawToken, StandardCharsets.UTF_8);

        int dotIndex = decodedToken.indexOf(".");
        if (dotIndex == -1) {
            throw new CustomException(BusinessErrorCodes.BAD_JWT_TOKEN);
        }

        String tokenId = decodedToken.substring(0, dotIndex);
        String rawTokenPart = decodedToken.substring(dotIndex + 1);


        RefreshTokenEntity token = refreshTokenRepo.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.BAD_JWT_TOKEN));


        if (passwordEncoder.matches(rawTokenPart, token.getToken())) {
            return token;
        }
        throw new CustomException(BusinessErrorCodes.BAD_JWT_TOKEN);
    }


    @Transactional
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token){
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.deleteById(token.getId());
            throw new CustomException(BusinessErrorCodes.TOKEN_EXPIRED);
        }
        return token;
    }
}