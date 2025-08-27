package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ArtistRepositoryIntegrationTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String base) {
        String uniq = String.valueOf(System.nanoTime());
        UserEntity u = UserEntity.builder()
                .fullName(base + " Full")
                .nickName((base + "_" + uniq).toLowerCase())
                .email((base + uniq).toLowerCase() + "@example.com")
                .password("secret")
                .enable(true)
                .build();
        em.persist(u);
        em.flush();
        return u;
    }

    private ArtistEntity persistArtist(String pseudonym) {
        UserEntity user = persistUser(pseudonym + "Owner");
        ArtistEntity a = ArtistEntity.builder()
                .pseudonym(pseudonym)
                .bannerImg("banner.jpg")
                .artistBio("bio " + pseudonym)
                .user(user)
                .build();
        em.persist(a);
        em.flush();
        return a;
    }

    @Test
    void findByPseudonym_returnsArtistWithUserLoaded() {
        ArtistEntity saved = persistArtist("TheBeatles");

        Set<ArtistEntity> found = artistRepository.findByPseudonym("TheBeatles");
        assertThat(found).hasSize(1);

        ArtistEntity a = found.iterator().next();
        assertThat(a.getId()).isEqualTo(saved.getId());
        assertThat(a.getUser()).isNotNull();
        assertThat(a.getUser().getId()).isEqualTo(saved.getUser().getId());
        assertThat(a.getUser().getNickName()).isNotNull();
    }

    @Test
    void findByPseudonym_returnsEmptyWhenMissing() {
        persistArtist("ExistingArtist");
        Set<ArtistEntity> found = artistRepository.findByPseudonym("NoSuchArtist");
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_pagination_returnsPageAndLoadsUser() {
        persistArtist("A_art");
        persistArtist("B_art");
        persistArtist("C_art");

        Page<ArtistEntity> page = artistRepository.findAll(PageRequest.of(0, 2, Sort.by("pseudonym").ascending()));

        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getContent()).hasSize(2);

        page.getContent().forEach(a -> {
            assertThat(a.getUser()).isNotNull();
            assertThat(a.getUser().getNickName()).isNotNull();
        });
    }
}