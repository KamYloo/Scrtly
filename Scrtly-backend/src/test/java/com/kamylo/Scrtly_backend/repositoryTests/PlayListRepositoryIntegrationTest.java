package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.playList.domain.PlayListEntity;
import com.kamylo.Scrtly_backend.playList.repository.PlayListRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PlayListRepositoryIntegrationTest {

    @Autowired
    private PlayListRepository playListRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        playListRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String name) {
        UserEntity user = UserEntity.builder()
                .fullName(name + " Owner")
                .nickName(name.toLowerCase() + "_nick")
                .email(name.toLowerCase() + "@example.com")
                .password("secret")
                .enable(true)
                .build();
        em.persist(user);
        em.flush();
        return user;
    }

    private PlayListEntity persistPlayList(String title, UserEntity user, boolean favourite) {
        PlayListEntity p = PlayListEntity.builder()
                .title(title)
                .coverImage("cover.jpg")
                .creationDate(LocalDate.now())
                .favourite(favourite)
                .user(user)
                .build();
        PlayListEntity saved = playListRepository.save(p);
        em.flush();
        return saved;
    }

    @Test
    void getPlayListsByUserId_returnsPagedResults_and_userLoaded() {
        UserEntity user1 = persistUser("UserOne");
        UserEntity user2 = persistUser("UserTwo");

        persistPlayList("A - First", user1, false);
        persistPlayList("B - Second", user1, true);
        persistPlayList("C - Third", user1, false);
        persistPlayList("Other - Only", user2, true);

        PageRequest pageable = PageRequest.of(0, 2, Sort.by("title").ascending());
        Page<PlayListEntity> page = playListRepository.getPlayListsByUserId(user1.getId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getContent()).hasSize(2);
        page.getContent().forEach(pl -> {
            assertThat(pl.getUser()).isNotNull();
            assertThat(pl.getUser().getNickName()).isEqualTo(user1.getNickName());
        });
    }

    @Test
    void getPlayListsByUserId_returnsEmptyForMissingUser() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<PlayListEntity> page = playListRepository.getPlayListsByUserId(9999L, pageable);

        assertThat(page.getTotalElements()).isEqualTo(0L);
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    void findByUserIdAndFavourite_returnsFavouriteWhenExists() {
        UserEntity user = persistUser("FavUser");

        persistPlayList("Not Fav", user, false);
        PlayListEntity fav = persistPlayList("My Favorite", user, true);

        Optional<PlayListEntity> res = playListRepository.findByUserIdAndFavourite(user.getId(), true);

        assertThat(res).isPresent();
        PlayListEntity found = res.get();
        assertThat(found.isFavourite()).isTrue();
        assertThat(found.getId()).isEqualTo(fav.getId());
        assertThat(found.getUser()).isNotNull();
        assertThat(found.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void findByUserIdAndFavourite_returnsEmptyWhenNone() {
        UserEntity user = persistUser("NoFavUser");

        persistPlayList("Playlist 1", user, false);
        persistPlayList("Playlist 2", user, false);

        Optional<PlayListEntity> res = playListRepository.findByUserIdAndFavourite(user.getId(), true);

        assertThat(res).isNotPresent();
    }
}