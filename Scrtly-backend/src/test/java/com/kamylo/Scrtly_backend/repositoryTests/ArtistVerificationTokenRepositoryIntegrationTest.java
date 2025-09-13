package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.artist.repository.ArtistVerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ArtistVerificationTokenRepositoryIntegrationTest {

    @Autowired
    private ArtistVerificationTokenRepository tokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testFindByUser_whenTokenExists() {
        UserEntity user = UserEntity.builder()
                .email("artist@example.com")
                .nickName("artist_nick")
                .fullName("Artist Example")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(user);

        ArtistVerificationToken token = new ArtistVerificationToken();
        token.setToken("verify-token-123");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        token.setRequestedArtistName("ExampleArtist");
        
        testEntityManager.persist(token);
        testEntityManager.flush();

        Optional<ArtistVerificationToken> foundToken = tokenRepository.findByUser(user);

        assertTrue(foundToken.isPresent());
        assertEquals("verify-token-123", foundToken.get().getToken());
    }

    @Test
    void testFindByUser_whenTokenDoesNotExist() {
        UserEntity user = UserEntity.builder()
                .email("newartist@example.com")
                .nickName("newartist_nick")
                .fullName("New Artist")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(user);
        testEntityManager.flush();

        Optional<ArtistVerificationToken> foundToken = tokenRepository.findByUser(user);
        assertFalse(foundToken.isPresent());
    }
}