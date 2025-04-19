package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.entity.ActivationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.ActivationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ActivationTokenRepositoryIntegrationTest {

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private UserEntity tokenUser;
    private UserEntity secondaryUser;

    @BeforeEach
    void setUp() {
        tokenUser = new UserEntity();
        tokenUser.setEmail("test@example.com");
        testEntityManager.persist(tokenUser);

        secondaryUser = new UserEntity();
        secondaryUser.setEmail("user2@example.com");
        testEntityManager.persist(secondaryUser);

        testEntityManager.flush();
    }

    @Test
    void findByToken_shouldReturnActivationToken_whenTokenExists() {
        ActivationToken token = new ActivationToken();
        token.setToken("abc123");
        token.setUser(tokenUser);
        token.setExpiryDate(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        activationTokenRepository.save(token);

        Optional<ActivationToken> result = activationTokenRepository.findByToken("abc123");

        assertTrue(result.isPresent());
        assertEquals("abc123", result.get().getToken());
        assertNotNull(result.get().getUser());
        assertEquals("test@example.com", result.get().getUser().getEmail());
    }

    @Test
    void findByUser_shouldReturnActivationToken_whenTokenExistsForUser() {
        ActivationToken token = new ActivationToken();
        token.setToken("def456");
        token.setUser(secondaryUser);
        token.setExpiryDate(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        activationTokenRepository.save(token);

        Optional<ActivationToken> result = activationTokenRepository.findByUser(secondaryUser);

        assertTrue(result.isPresent());
        assertEquals("def456", result.get().getToken());
        assertEquals("user2@example.com", result.get().getUser().getEmail());
    }

    @Test
    void findByToken_shouldReturnEmpty_whenTokenDoesNotExist() {
        Optional<ActivationToken> result = activationTokenRepository.findByToken("nonexistent");
        assertFalse(result.isPresent());
    }

    @Test
    void findByUser_shouldReturnEmpty_whenNoTokenAssociatedWithUser() {
        UserEntity noTokenUser = new UserEntity();
        noTokenUser.setEmail("no.token@example.com");
        testEntityManager.persist(noTokenUser);
        testEntityManager.flush();

        Optional<ActivationToken> result = activationTokenRepository.findByUser(noTokenUser);
        assertFalse(result.isPresent());
    }
}