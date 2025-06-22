package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.auth.domain.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.auth.repository.RefreshTokenRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.auth.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepo;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private final String email = "test@example.com";

    @Test
    void testCreateRefreshToken_NewToken() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(refreshTokenRepo.findByUser(userEntity)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");

        RefreshTokenResponse response = refreshTokenService.createRefreshToken(email);

        assertNotNull(response);
        assertTrue(response.getRefreshToken().contains("."));
        verify(refreshTokenRepo, times(1)).save(any(RefreshTokenEntity.class));
    }

    @Test
    void testCreateRefreshToken_UpdateExistingToken() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        RefreshTokenEntity existingToken = RefreshTokenEntity.builder().tokenId("oldTokenId").token("oldHashed").expiryDate(Instant.now()).user(userEntity).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(refreshTokenRepo.findByUser(userEntity)).thenReturn(Optional.of(existingToken));
        when(passwordEncoder.encode(anyString())).thenReturn("newHashedToken");

        RefreshTokenResponse response = refreshTokenService.createRefreshToken(email);

        assertNotNull(response);
        assertTrue(response.getRefreshToken().contains("."));
        verify(refreshTokenRepo, times(1)).save(existingToken);
    }

    @Test
    void testCreateRefreshToken_UserNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> refreshTokenService.createRefreshToken(email));
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testDeleteByUserEmail_Success() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        refreshTokenService.deleteByUserEmail(email);

        verify(refreshTokenRepo, times(1)).deleteByUser(userEntity);
    }

    @Test
    void testDeleteByUserEmail_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> refreshTokenService.deleteByUserEmail(email));
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testFindByToken_ValidToken() {
        String tokenId = UUID.randomUUID().toString();
        String rawTokenPart = UUID.randomUUID().toString();
        String refreshToken = tokenId + "." + rawTokenPart;
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .id(1L)
                .user(userEntity)
                .tokenId(tokenId)
                .token("hashedToken")
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepo.findByTokenId(tokenId)).thenReturn(Optional.of(tokenEntity));
        when(passwordEncoder.matches(rawTokenPart, tokenEntity.getToken())).thenReturn(true);

        RefreshTokenEntity result = refreshTokenService.findByToken(refreshToken);

        assertNotNull(result);
        assertEquals(tokenEntity, result);
    }

    @Test
    void testFindByToken_InvalidFormat() {
        String invalidToken = "invalidTokenWithoutDot";
        CustomException thrown = assertThrows(CustomException.class,
                () -> refreshTokenService.findByToken(invalidToken));
        assertEquals(BusinessErrorCodes.BAD_JWT_TOKEN, thrown.getErrorCode());
    }

    @Test
    void testFindByToken_TokenNotFound() {
        String tokenId = UUID.randomUUID().toString();
        String rawTokenPart = UUID.randomUUID().toString();
        String refreshToken = tokenId + "." + rawTokenPart;
        when(refreshTokenRepo.findByTokenId(tokenId)).thenReturn(Optional.empty());

        CustomException thrown = assertThrows(CustomException.class,
                () -> refreshTokenService.findByToken(refreshToken));
        assertEquals(BusinessErrorCodes.BAD_JWT_TOKEN, thrown.getErrorCode());
    }

    @Test
    void testFindByToken_PasswordNotMatch() {
        String tokenId = UUID.randomUUID().toString();
        String rawTokenPart = UUID.randomUUID().toString();
        String refreshToken = tokenId + "." + rawTokenPart;
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .id(2L)
                .user(userEntity)
                .tokenId(tokenId)
                .token("hashedToken")
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepo.findByTokenId(tokenId)).thenReturn(Optional.of(tokenEntity));
        when(passwordEncoder.matches(rawTokenPart, tokenEntity.getToken())).thenReturn(false);

        CustomException thrown = assertThrows(CustomException.class,
                () -> refreshTokenService.findByToken(refreshToken));
        assertEquals(BusinessErrorCodes.BAD_JWT_TOKEN, thrown.getErrorCode());
    }


    @Test
    void testVerifyExpiration_NotExpired() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .id(3L)
                .user(userEntity)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        RefreshTokenEntity result = refreshTokenService.verifyExpiration(tokenEntity);

        assertNotNull(result);
        assertEquals(tokenEntity, result);
    }

    @Test
    void testVerifyExpiration_Expired() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .id(4L)
                .user(userEntity)
                .expiryDate(Instant.now().minusSeconds(10))
                .build();

        CustomException thrown = assertThrows(CustomException.class,
                () -> refreshTokenService.verifyExpiration(tokenEntity));
        assertEquals(BusinessErrorCodes.TOKEN_EXPIRED, thrown.getErrorCode());
        verify(refreshTokenRepo, times(1)).deleteById(tokenEntity.getId());
    }
}