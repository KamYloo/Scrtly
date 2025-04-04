package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.response.LoginResponseDto;
import com.kamylo.Scrtly_backend.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.entity.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.repository.RefreshTokenRepository;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
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
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                ()->new RuntimeException("User not found"));

        String tokenId = UUID.randomUUID().toString();
        String rawTokenPart = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawTokenPart);
        System.out.println("jebac "+hashedToken);

        RefreshTokenEntity tokenEntity  = RefreshTokenEntity.builder()
                .user(userEntity)
                .tokenId(tokenId)
                .token(hashedToken)
                .expiryDate(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000))
                .build();
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
        System.out.println("Decoded token: " + decodedToken);

        int dotIndex = decodedToken.indexOf(".");
        if (dotIndex == -1) {
            throw new CustomException(BusinessErrorCodes.BAD_JWT_TOKEN);
        }

        String tokenId = decodedToken.substring(0, dotIndex);
        String rawTokenPart = decodedToken.substring(dotIndex + 1);

        System.out.println("Extracted tokenId: " + tokenId);
        System.out.println("Extracted rawTokenPart: " + rawTokenPart);

        RefreshTokenEntity token = refreshTokenRepo.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.BAD_JWT_TOKEN));


        if (passwordEncoder.matches(rawTokenPart, token.getToken())) {
            return token;
        }
        throw new CustomException(BusinessErrorCodes.BAD_JWT_TOKEN);
    }


    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token){
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new CustomException(BusinessErrorCodes.TOKEN_EXPIRED);
        }
        return token;
    }
}