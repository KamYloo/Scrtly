package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.auth.domain.ActivationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.auth.repository.ActivationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

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
        tokenUser = UserEntity.builder()
                .email("test@example.com")
                .nickName("testnick")
                .fullName("Test User")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(tokenUser);

        secondaryUser = UserEntity.builder()
                .email("user2@example.com")
                .nickName("user2")
                .fullName("User Two")
                .password("secret")
                .enable(true)
                .build();
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
        UserEntity noTokenUser = UserEntity.builder()
                .email("no.token@example.com")
                .nickName("notoken")
                .fullName("No Token")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(noTokenUser);
        testEntityManager.flush();

        Optional<ActivationToken> result = activationTokenRepository.findByUser(noTokenUser);
        assertFalse(result.isPresent());
    }
}
