package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.auth.domain.PasswordResetToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.auth.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PasswordResetTokenRepositoryIntegrationTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private UserEntity userWithToken;
    private UserEntity userWithoutToken;

    @BeforeEach
    void setUp() {
        userWithToken = new UserEntity();
        userWithToken.setEmail("user.token@example.com");
        testEntityManager.persist(userWithToken);

        userWithoutToken = new UserEntity();
        userWithoutToken.setEmail("user.notoken@example.com");
        testEntityManager.persist(userWithoutToken);

        testEntityManager.flush();
    }

    @Test
    void testFindByUser_whenTokenExists() {
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("reset-token-123")
                .user(userWithToken)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

        testEntityManager.persist(resetToken);
        testEntityManager.flush();

        PasswordResetToken foundToken = tokenRepository.findByUser(userWithToken);

        assertNotNull(foundToken);
        assertEquals("reset-token-123", foundToken.getToken());
    }

    @Test
    void testFindByUser_whenTokenDoesNotExist() {
        PasswordResetToken foundToken = tokenRepository.findByUser(userWithoutToken);
        assertNull(foundToken);
    }
}