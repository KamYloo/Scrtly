package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.story.domain.StoryEntity;
import com.kamylo.Scrtly_backend.story.repository.StoryRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StoryRepositoryIntegrationTest {

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        storyRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String base) {
        String uniq = String.valueOf(System.nanoTime());
        UserEntity user = UserEntity.builder()
                .fullName(base + " Full")
                .nickName((base + "_" + uniq).toLowerCase())
                .email((base + uniq).toLowerCase() + "@example.com")
                .password("secret")
                .enable(true)
                .followers(new HashSet<>())
                .followings(new HashSet<>())
                .build();
        em.persist(user);
        em.flush();
        return user;
    }

    private void persistStory(String image, UserEntity user) {
        StoryEntity s = StoryEntity.builder()
                .image(image)
                .timestamp(LocalDateTime.now())
                .user(user)
                .build();
        em.persist(s);
        em.flush();
    }

    @Test
    void getStoriesByUserId_returnsStoriesWithUserLoaded() {
        UserEntity author = persistUser("AuthorA");
        persistStory("img1.jpg", author);
        persistStory("img2.jpg", author);
        persistStory("img3.jpg", author);

        persistStory("other.jpg", persistUser("Other"));

        List<StoryEntity> results = storyRepository.getStoriesByUserId(author.getId());

        assertThat(results).hasSize(3);
        assertThat(results).allSatisfy(s -> {
            assertThat(s.getUser()).isNotNull();
            assertThat(s.getUser().getId()).isEqualTo(author.getId());
            assertThat(s.getUser().getNickName()).isEqualTo(author.getNickName());
        });
    }

    @Test
    void getStoriesByFollowedUsers_returnsStoriesOfFollowedAuthorsOnly() {
        UserEntity follower = persistUser("Follower");
        UserEntity author1 = persistUser("Author1");
        UserEntity author2 = persistUser("Author2");

        author1.getFollowers().add(follower);
        em.persist(author1);
        em.flush();

        persistStory("a1.jpg", author1);
        persistStory("a2.jpg", author2);

        List<StoryEntity> results = storyRepository.getStoriesByFollowedUsers(follower.getId());

        assertThat(results).hasSize(1);
        StoryEntity s = results.get(0);
        assertThat(s.getUser()).isNotNull();
        assertThat(s.getUser().getId()).isEqualTo(author1.getId());
        assertThat(s.getUser().getNickName()).isEqualTo(author1.getNickName());
    }

    @Test
    void getStoriesByFollowedUsers_returnsEmptyWhenNoFollowings() {
        UserEntity viewer = persistUser("Viewer");
        UserEntity author = persistUser("AuthorX");

        persistStory("img.jpg", author);

        List<StoryEntity> results = storyRepository.getStoriesByFollowedUsers(viewer.getId());

        assertThat(results).isEmpty();
    }
}