package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.auth.domain.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RefreshTokenRepositoryIntegrationTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("refreshuser@example.com");
        testEntityManager.persist(user);
        testEntityManager.flush();
    }

    @Test
    void testFindByToken_whenExists() {
        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .tokenId("unique-token-id")
                .token("sampleToken")
                .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .user(user)
                .build();

        testEntityManager.persist(tokenEntity);
        testEntityManager.flush();

        Optional<RefreshTokenEntity> result = refreshTokenRepository.findByToken("sampleToken");
        assertTrue(result.isPresent());
        assertEquals("sampleToken", result.get().getToken());
    }

    @Test
    void testFindByUser_whenExists() {
        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .tokenId("unique-token-id-2")
                .token("tokenForUser")
                .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .user(user)
                .build();

        testEntityManager.persist(tokenEntity);
        testEntityManager.flush();

        Optional<RefreshTokenEntity> result = refreshTokenRepository.findByUser(user);
        assertTrue(result.isPresent());
        assertEquals("tokenForUser", result.get().getToken());
    }

    @Test
    void testFindByTokenId_whenExists() {
        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .tokenId("token-id-xyz")
                .token("anotherToken")
                .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .user(user)
                .build();

        testEntityManager.persist(tokenEntity);
        testEntityManager.flush();

        Optional<RefreshTokenEntity> result = refreshTokenRepository.findByTokenId("token-id-xyz");
        assertTrue(result.isPresent());
        assertEquals("anotherToken", result.get().getToken());
    }

    @Test
    void testDeleteByUser() {
        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .tokenId("delete-token-id")
                .token("deleteToken")
                .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .user(user)
                .build();

        testEntityManager.persist(tokenEntity);
        testEntityManager.flush();

        Optional<RefreshTokenEntity> resultBefore = refreshTokenRepository.findByUser(user);
        assertTrue(resultBefore.isPresent());

        refreshTokenRepository.deleteByUser(user);
        testEntityManager.flush();

        Optional<RefreshTokenEntity> resultAfter = refreshTokenRepository.findByUser(user);
        assertFalse(resultAfter.isPresent());
    }
}