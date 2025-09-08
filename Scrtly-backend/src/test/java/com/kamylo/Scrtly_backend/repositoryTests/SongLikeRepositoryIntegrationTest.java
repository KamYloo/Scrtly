package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.like.domain.SongLikeEntity;
import com.kamylo.Scrtly_backend.like.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SongLikeRepositoryIntegrationTest {

    @Autowired
    private SongLikeRepository songLikeRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        songLikeRepository.deleteAll();
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

    private ArtistEntity persistArtist(String name) {
        UserEntity user = persistUser(name + "Owner");
        ArtistEntity a = ArtistEntity.builder()
                .pseudonym(name)
                .bannerImg("banner.jpg")
                .artistBio("bio")
                .user(user)
                .build();
        em.persist(a);
        em.flush();
        return a;
    }

    private AlbumEntity persistAlbum(String title, ArtistEntity artist) {
        AlbumEntity alb = AlbumEntity.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .coverImage("cover.jpg")
                .artist(artist)
                .build();
        em.persist(alb);
        em.flush();
        return alb;
    }

    private SongEntity persistSong(String title, AlbumEntity album, ArtistEntity artist) {
        SongEntity s = SongEntity.builder()
                .title(title)
                .track("track.mp3")
                .imageSong("img.jpg")
                .contentType("audio/mpeg")
                .duration(123)
                .album(album)
                .artist(artist)
                .build();
        s.setLikes(new HashSet<>());
        s.setPlaylists(new HashSet<>());
        em.persist(s);
        em.flush();
        return s;
    }

    private SongLikeEntity persistSongLike(UserEntity user, SongEntity song) {
        SongLikeEntity sl = SongLikeEntity.builder()
                .user(user)
                .song(song)
                .build();
        em.persist(sl);
        if (song.getLikes() == null) song.setLikes(new HashSet<>());
        song.getLikes().add(sl);
        em.merge(song);
        em.flush();
        return sl;
    }

    @Test
    void findByUserIdAndSongId_returnsLikeWhenExists_andNullWhenAbsent() {
        UserEntity user = persistUser("LikerA");
        ArtistEntity artist = persistArtist("ArtistA");
        AlbumEntity album = persistAlbum("AlbumA", artist);
        SongEntity song = persistSong("SongA", album, artist);

        SongLikeEntity none = songLikeRepository.findByUserIdAndSongId(user.getId(), song.getId());
        assertThat(none).isNull();

        SongLikeEntity saved = persistSongLike(user, song);

        SongLikeEntity found = songLikeRepository.findByUserIdAndSongId(user.getId(), song.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUser()).isNotNull();
        assertThat(found.getSong()).isNotNull();
        assertThat(found.getSong().getId()).isEqualTo(song.getId());
    }

    @Test
    void deleteBySong_removesAllLikesForThatSong_onlyAffectsTargetSong() {
        UserEntity u1 = persistUser("U1");
        UserEntity u2 = persistUser("U2");
        ArtistEntity artist = persistArtist("ArtistB");
        AlbumEntity album = persistAlbum("AlbumB", artist);
        SongEntity song1 = persistSong("Song1", album, artist);
        SongEntity song2 = persistSong("Song2", album, artist);

        persistSongLike(u1, song1);
        persistSongLike(u2, song1);
        persistSongLike(u1, song2);

        List<SongLikeEntity> beforeAll = songLikeRepository.findAll();
        assertThat(beforeAll).hasSize(3);

        songLikeRepository.deleteBySong(song1);
        em.flush();
        em.clear();

        List<SongLikeEntity> remaining = songLikeRepository.findAll();
        assertThat(remaining).hasSize(1);
    }
}