package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.album.repository.AlbumSpecification;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
@ActiveProfiles("test")
class AlbumRepositoryIntegrationTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
        em.flush();
    }

    private ArtistEntity persistArtist(String name) {
        UserEntity user = UserEntity.builder()
                .fullName(name + " owner")
                .nickName(name.toLowerCase() + "_nick")
                .email(name.toLowerCase()+"@example.com")
                .password("secret")
                .enable(true)
                .build();
        em.persist(user);
        em.flush();

        ArtistEntity artist = ArtistEntity.builder()
                .pseudonym(name)
                .user(user)
                .build();

        em.persist(artist);
        em.flush();
        return artist;
    }


    private void persistAlbum(String title, ArtistEntity artist) {
        AlbumEntity album = AlbumEntity.builder()
                .title(title)
                .artist(artist)
                .releaseDate(LocalDate.now())
                .coverImage("cover.jpg")
                .build();
        albumRepository.save(album);
        em.flush();
    }

    @Test
    void findByArtistId_returnsAlbumsWithArtist() {
        ArtistEntity artist = persistArtist("TheBeatles");
        persistAlbum("Abbey Road", artist);
        persistAlbum("Let It Be", artist);

        List<AlbumEntity> results = albumRepository.findByArtistId(artist.getId());

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(a -> a.getArtist().getPseudonym())
                .allMatch(name -> name.equals("TheBeatles"));
    }

    @Test
    void findAll_withTitleSpecification_filtersAndPaginates() {
        ArtistEntity a = persistArtist("SomeArtist");
        persistAlbum("Love Song", a);
        persistAlbum("Another Love", a);
        persistAlbum("No Match", a);

        Specification<AlbumEntity> spec = AlbumSpecification.titleContains("love");

        Page<AlbumEntity> page = albumRepository.findAll(spec, PageRequest.of(0, 2, Sort.by("title").ascending()));

        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent())
                .extracting(AlbumEntity::getTitle)
                .allMatch(t -> t.toLowerCase().contains("love"));
    }

    @Test
    void findAll_withArtistSpecification_caseInsensitive() {
        ArtistEntity beatles = persistArtist("TheBeatles");
        ArtistEntity queen = persistArtist("Queen");

        persistAlbum("Help!", beatles);
        persistAlbum("Bohemian Rhapsody", queen);

        Specification<AlbumEntity> spec = AlbumSpecification.artistContains("beatles");

        Page<AlbumEntity> page = albumRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getArtist().getPseudonym()).isEqualTo("TheBeatles");
    }

    @Test
    void specifications_nullOrEmptyReturnAll() {
        ArtistEntity a = persistArtist("X");
        persistAlbum("One", a);
        persistAlbum("Two", a);

        Specification<AlbumEntity> spec = Specification.where(AlbumSpecification.titleContains(null))
                .and(AlbumSpecification.artistContains(""));

        Page<AlbumEntity> page = albumRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2L);
    }

    @Test
    void specifications_combinedTitleAndArtist() {
        ArtistEntity special = persistArtist("SpecialArtist");
        ArtistEntity other = persistArtist("OtherArtist");

        persistAlbum("Unique Title", special);
        persistAlbum("Unique Title", other);
        persistAlbum("Another Title", special);

        Specification<AlbumEntity> spec = Specification.where(AlbumSpecification.titleContains("unique"))
                .and(AlbumSpecification.artistContains("special"));

        Page<AlbumEntity> page = albumRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getArtist().getPseudonym()).isEqualTo("SpecialArtist");
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Unique Title");
    }
}
