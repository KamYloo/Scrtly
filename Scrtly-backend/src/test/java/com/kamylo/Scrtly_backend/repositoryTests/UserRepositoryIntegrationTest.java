package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testFindByEmail() {
        UserEntity user = UserEntity.builder()
                .fullName("John Doe")
                .nickName("jdoe")
                .email("john.doe@example.com")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(user);
        testEntityManager.flush();

        Optional<UserEntity> found = userRepository.findByEmail("john.doe@example.com");

        assertTrue(found.isPresent());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    void testFindByNickName() {
        UserEntity user = UserEntity.builder()
                .fullName("Jane Smith")
                .nickName("janes")
                .email("jane.smith@example.com")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(user);
        testEntityManager.flush();

        Optional<UserEntity> found = userRepository.findByNickName("janes");

        assertTrue(found.isPresent());
        assertEquals("janes", found.get().getNickName());
    }

    @Test
    void testSearchUser() {
        UserEntity user1 = UserEntity.builder()
                .fullName("Alice Wonderland")
                .nickName("alice")
                .email("alice@example.com")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(user1);

        UserEntity user2 = UserEntity.builder()
                .fullName("Bob Builder")
                .nickName("bobby")
                .email("bob@example.com")
                .password("secret")
                .enable(true)
                .build();
        testEntityManager.persist(user2);

        testEntityManager.flush();

        Set<UserEntity> results = userRepository.searchUser("Alice");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(u -> u.getEmail().equals("alice@example.com")));

        results = userRepository.searchUser("bobby");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(u -> u.getEmail().equals("bob@example.com")));

        results = userRepository.searchUser("nonexistent");
        assertTrue(results.isEmpty());
    }
}