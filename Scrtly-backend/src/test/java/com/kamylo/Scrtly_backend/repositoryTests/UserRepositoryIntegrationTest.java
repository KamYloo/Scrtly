package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private UserEntity createUser(String email, String nickname, String fullName) {
        return UserEntity.builder()
                .email(email)
                .nickName(nickname)
                .fullName(fullName)
                .password("password123")
                .enable(true)
                .followers(new HashSet<>())
                .followings(new HashSet<>())
                .roles(new HashSet<>())
                .build();
    }

    @Test
    void testFindByEmail() {
        UserEntity user = createUser("john.doe@example.com", "jdoe", "John Doe");
        testEntityManager.persist(user);
        testEntityManager.flush();

        Optional<UserEntity> found = userRepository.findByEmail("john.doe@example.com");

        assertTrue(found.isPresent());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    void testFindByNickName() {
        UserEntity user = createUser("jane.smith@example.com", "janes", "Jane Smith");
        testEntityManager.persist(user);
        testEntityManager.flush();

        Optional<UserEntity> found = userRepository.findByNickName("janes");

        assertTrue(found.isPresent());
        assertEquals("janes", found.get().getNickName());
    }

    @Test
    void testSearchUser() {
        UserEntity user1 = createUser("alice@example.com", "alice", "Alice Wonderland");
        testEntityManager.persist(user1);

        UserEntity user2 = createUser("bob@example.com", "bobby", "Bob Builder");
        testEntityManager.persist(user2);

        testEntityManager.flush();

        Set<UserEntity> resultsAlice = userRepository.searchUser("Alice");
        assertThat(resultsAlice).isNotEmpty();
        assertThat(resultsAlice).extracting(UserEntity::getEmail).contains("alice@example.com");

        Set<UserEntity> resultsBob = userRepository.searchUser("bobby");
        assertThat(resultsBob).isNotEmpty();
        assertThat(resultsBob).extracting(UserEntity::getEmail).contains("bob@example.com");

        Set<UserEntity> resultsNone = userRepository.searchUser("nonexistent");
        assertThat(resultsNone).isEmpty();
    }

    @Test
    void testFollowersAndFollowingsCounts() {
        UserEntity userToFollow = createUser("star@example.com", "star", "Star User");
        UserEntity fan1 = createUser("fan1@example.com", "fan1", "Fan One");
        UserEntity fan2 = createUser("fan2@example.com", "fan2", "Fan Two");

        testEntityManager.persist(userToFollow);
        testEntityManager.persist(fan1);
        testEntityManager.persist(fan2);

        userToFollow.getFollowers().add(fan1);
        userToFollow.getFollowers().add(fan2);

        testEntityManager.persistAndFlush(userToFollow);

        testEntityManager.clear();

        long followersCount = userRepository.countFollowers(userToFollow.getId());
        long fan1FollowingsCount = userRepository.countFollowings(fan1.getId());

        assertEquals(2, followersCount, "User should have 2 followers");
        assertEquals(1, fan1FollowingsCount, "Fan1 should have 1 following");
    }

    @Test
    void testIsFollowedBy() {
        UserEntity celebrity = createUser("celeb@example.com", "celeb", "Celebrity");
        UserEntity follower = createUser("follower@example.com", "follower", "Follower");
        UserEntity stranger = createUser("stranger@example.com", "stranger", "Stranger");

        testEntityManager.persist(celebrity);
        testEntityManager.persist(follower);
        testEntityManager.persist(stranger);

        celebrity.getFollowers().add(follower);
        testEntityManager.persistAndFlush(celebrity);
        testEntityManager.clear();

        boolean isFollowed = userRepository.isFollowedBy(celebrity.getId(), follower.getId());
        assertTrue(isFollowed, "Should return true when relationship exists");

        boolean isNotFollowed = userRepository.isFollowedBy(celebrity.getId(), stranger.getId());
        assertFalse(isNotFollowed, "Should return false when relationship does not exist");

        boolean reverseCheck = userRepository.isFollowedBy(follower.getId(), celebrity.getId());
        assertFalse(reverseCheck, "Relationship should not be symmetric unless explicitly set");
    }

    @Test
    void testFindFollowersByUserId_withSearch() {
        UserEntity user = createUser("user@example.com", "mainUser", "Main User");

        UserEntity followerA = createUser("alice@example.com", "alice", "Alice Wonderland");
        UserEntity followerB = createUser("alex@example.com", "alex", "Alex The Lion");
        UserEntity followerC = createUser("bob@example.com", "bob", "Bob Builder");

        testEntityManager.persist(user);
        testEntityManager.persist(followerA);
        testEntityManager.persist(followerB);
        testEntityManager.persist(followerC);

        user.getFollowers().add(followerA);
        user.getFollowers().add(followerB);
        user.getFollowers().add(followerC);
        testEntityManager.persistAndFlush(user);

        testEntityManager.clear();

        Page<UserEntity> results = userRepository.findFollowersByUserId(
                user.getId(),
                "Al",
                PageRequest.of(0, 10)
        );

        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent())
                .extracting(UserEntity::getNickName)
                .containsExactlyInAnyOrder("alice", "alex");

        assertThat(results.getContent())
                .extracting(UserEntity::getNickName)
                .doesNotContain("bob");
    }
}