package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.playList.domain.PlayListEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SongRepositoryIntegrationTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();
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

    private ArtistEntity persistArtist(String name) {
        UserEntity user = persistUser(name + "User");
        ArtistEntity artist = ArtistEntity.builder()
                .pseudonym(name)
                .bannerImg("banner.jpg")
                .artistBio(name + " bio")
                .user(user)
                .build();
        em.persist(artist);
        em.flush();
        return artist;
    }

    private AlbumEntity persistAlbum(String title, ArtistEntity artist) {
        AlbumEntity album = AlbumEntity.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .coverImage("cover.jpg")
                .artist(artist)
                .build();
        em.persist(album);
        em.flush();
        return album;
    }

    private SongEntity persistSong(String title, AlbumEntity album, ArtistEntity artist) {
        SongEntity song = SongEntity.builder()
                .title(title)
                .track(title.toLowerCase().replaceAll("\\s+", "_") + ".mp3")
                .imageSong("img.jpg")
                .album(album)
                .artist(artist)
                .contentType("audio/mpeg")
                .duration(180)
                .favorite(false)
                .build();
        SongEntity saved = songRepository.save(song);
        em.flush();
        return saved;
    }

    private PlayListEntity persistPlaylist(UserEntity user, Set<SongEntity> songs) {
        PlayListEntity p = PlayListEntity.builder()
                .title("My Playlist")
                .coverImage("cover.jpg")
                .creationDate(LocalDate.now())
                .favourite(false)
                .user(user)
                .songs(songs)
                .build();
        em.persist(p);
        em.flush();
        return p;
    }

    @Test
    void findByTitle_returnsCaseInsensitiveMatches() {
        ArtistEntity artist = persistArtist("ArtistA");
        AlbumEntity album = persistAlbum("Album A", artist);

        persistSong("Love Story", album, artist);
        persistSong("Another love", album, artist);
        persistSong("No Match", album, artist);

        Set<SongEntity> results = songRepository.findByTitle("LoVe");

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(SongEntity::getTitle)
                .allMatch(t -> t.toLowerCase().contains("love"));
    }

    @Test
    void findByTitle_noMatches_returnsEmpty() {
        ArtistEntity artist = persistArtist("ArtistB");
        AlbumEntity album = persistAlbum("Album B", artist);
        persistSong("Some Song", album, artist);

        Set<SongEntity> results = songRepository.findByTitle("xxx_not_present_xxx");
        assertThat(results).isEmpty();
    }

    @Test
    void findByArtistId_returnsPagedResults_and_artistLoaded() {
        ArtistEntity artistA = persistArtist("ArtistC");
        AlbumEntity albumA = persistAlbum("Album C1", artistA);

        persistSong("A Song", albumA, artistA);
        persistSong("B Song", albumA, artistA);
        persistSong("C Song", albumA, artistA);

        ArtistEntity artistB = persistArtist("ArtistD");
        AlbumEntity albumB = persistAlbum("Album D1", artistB);
        persistSong("Other Song", albumB, artistB);

        PageRequest pageable = PageRequest.of(0, 2, Sort.by("title").ascending());
        Page<SongEntity> page = songRepository.findByArtistId(artistA.getId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getContent()).hasSize(2);
        page.getContent().forEach(s -> {
            assertThat(s.getArtist()).isNotNull();
            assertThat(s.getArtist().getPseudonym()).isEqualTo("ArtistC");
            assertThat(s.getAlbum()).isNotNull();
        });
    }

    @Test
    void findByPlaylistId_returnsSongsLinkedToPlaylist() {
        ArtistEntity artist = persistArtist("ArtistE");
        AlbumEntity album = persistAlbum("Album E", artist);

        SongEntity s1 = persistSong("Playlist Song 1", album, artist);
        SongEntity s2 = persistSong("Playlist Song 2", album, artist);

        UserEntity user = persistUser("PlaylistOwner");
        Set<SongEntity> songs = new HashSet<>();
        // ensure we attach managed song instances
        songs.add(em.find(SongEntity.class, s1.getId()));
        songs.add(em.find(SongEntity.class, s2.getId()));

        PlayListEntity playlist = persistPlaylist(user, songs);

        Page<SongEntity> page = songRepository.findByPlaylistId(playlist.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2L);
        List<SongEntity> content = page.getContent();
        assertThat(content).extracting(SongEntity::getId).containsExactlyInAnyOrder(s1.getId(), s2.getId());
        content.forEach(s -> {
            assertThat(s.getAlbum()).isNotNull();
            assertThat(s.getArtist()).isNotNull();
        });
    }

    @Test
    void findByAlbumId_returnsAllSongsForAlbum() {
        ArtistEntity artist = persistArtist("ArtistF");
        AlbumEntity album1 = persistAlbum("Album F1", artist);
        AlbumEntity album2 = persistAlbum("Album F2", artist);

        SongEntity a1 = persistSong("Album1 Song1", album1, artist);
        SongEntity a2 = persistSong("Album1 Song2", album1, artist);
        persistSong("Album2 Song1", album2, artist);

        List<SongEntity> results = songRepository.findByAlbumId(album1.getId());

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(s -> s.getAlbum().getId())
                .allMatch(id -> id.equals(album1.getId()));
        assertThat(results).extracting(SongEntity::getId).containsExactlyInAnyOrder(a1.getId(), a2.getId());
    }
}