package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.like.domain.SongLikeEntity;
import com.kamylo.Scrtly_backend.like.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SongLikeRepositoryIntegrationTest {

    @Autowired private SongLikeRepository songLikeRepository;
    @Autowired private SongRepository songRepository;
    @Autowired private AlbumRepository albumRepository;
    @Autowired private ArtistRepository artistRepository;
    @Autowired private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        songLikeRepository.deleteAll();
        songRepository.deleteAll();
        albumRepository.deleteAll();
        artistRepository.deleteAll();
        userRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String base) {
        UserEntity u = UserEntity.builder()
                .fullName(base + " Full")
                .nickName(base.toLowerCase() + "_nick")
                .email(base.toLowerCase() + "@example.com")
                .password("secret")
                .enable(true)
                .build();
        return em.persistAndFlush(u);
    }

    private ArtistEntity persistArtist(String name) {
        UserEntity user = persistUser(name + "Owner");
        ArtistEntity a = ArtistEntity.builder()
                .pseudonym(name)
                .bannerImg("banner.jpg")
                .artistBio("bio")
                .user(user)
                .build();
        return em.persistAndFlush(a);
    }

    private AlbumEntity persistAlbum(String title, ArtistEntity artist) {
        AlbumEntity alb = AlbumEntity.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .coverImage("cover.jpg")
                .artist(artist)
                .build();
        return em.persistAndFlush(alb);
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
        return em.persistAndFlush(s);
    }

    private SongLikeEntity persistSongLike(UserEntity user, SongEntity song) {
        SongLikeEntity sl = SongLikeEntity.builder()
                .user(user)
                .song(song)
                .build();

        sl = em.persistAndFlush(sl);

        if (song.getLikes() == null)
            song.setLikes(new HashSet<>());
        song.getLikes().add(sl);
        em.merge(song);

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
        assertThat(found.getUser().getId()).isEqualTo(user.getId());
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

        em.flush();
        em.clear();

        List<SongLikeEntity> beforeAll = songLikeRepository.findAll();
        assertThat(beforeAll).hasSize(3);

        songLikeRepository.deleteBySong(song1);

        em.flush();
        em.clear();

        List<SongLikeEntity> remaining = songLikeRepository.findAll();
        assertThat(remaining).hasSize(1);

        assertThat(remaining.getFirst().getSong().getId()).isEqualTo(song2.getId());
    }

    @Test
    void findSongIdsLikedByUser_returnsOnlyIdsOfSongsLikedByUser() {
        UserEntity user = persistUser("MainUser");
        UserEntity otherUser = persistUser("OtherUser");

        ArtistEntity artist = persistArtist("ArtistC");
        AlbumEntity album = persistAlbum("AlbumC", artist);

        SongEntity song1 = persistSong("S1", album, artist);
        SongEntity song2 = persistSong("S2", album, artist);
        SongEntity song3 = persistSong("S3", album, artist);

        persistSongLike(user, song1);
        persistSongLike(user, song3);

        persistSongLike(otherUser, song2);

        em.flush();
        em.clear();

        List<Long> songsToCheck = List.of(song1.getId(), song2.getId(), song3.getId());

        Set<Long> likedIds = songLikeRepository.findSongIdsLikedByUser(user.getId(), songsToCheck);

        assertThat(likedIds).hasSize(2);
        assertThat(likedIds).containsExactlyInAnyOrder(song1.getId(), song3.getId());
        assertThat(likedIds).doesNotContain(song2.getId());
    }

    @Test
    void deleteByUserAndSongs_removesSpecificLikes_preservesOthersAndOtherUsers() {
        UserEntity user = persistUser("TargetUser");
        UserEntity otherUser = persistUser("OtherUser");

        ArtistEntity artist = persistArtist("ArtistD");
        AlbumEntity album = persistAlbum("AlbumD", artist);

        SongEntity s1 = persistSong("S1_DeleteMe", album, artist);
        SongEntity s2 = persistSong("S2_DeleteMe", album, artist);
        SongEntity s3 = persistSong("S3_KeepMe", album, artist);

        persistSongLike(user, s1);
        persistSongLike(user, s2);
        SongLikeEntity likeToKeep = persistSongLike(user, s3);

        SongLikeEntity otherLike1 = persistSongLike(otherUser, s1);
        SongLikeEntity otherLike2 = persistSongLike(otherUser, s2);

        em.flush();
        em.clear();

        UserEntity userRef = userRepository.findById(user.getId()).orElseThrow();
        SongEntity s1Ref = songRepository.findById(s1.getId()).orElseThrow();
        SongEntity s2Ref = songRepository.findById(s2.getId()).orElseThrow();

        songLikeRepository.deleteByUserAndSongs(userRef, List.of(s1Ref, s2Ref));

        em.flush();
        em.clear();

        List<SongLikeEntity> remainingLikes = songLikeRepository.findAll();

        assertThat(remainingLikes).hasSize(3);

        boolean userHasDeletedLikes = remainingLikes.stream()
                .anyMatch(sl -> sl.getUser().getId().equals(user.getId()) &&
                        (sl.getSong().getId().equals(s1.getId()) || sl.getSong().getId().equals(s2.getId())));

        assertThat(userHasDeletedLikes).isFalse();

        boolean userHasKeptLike = remainingLikes.stream()
                .anyMatch(sl -> sl.getUser().getId().equals(user.getId()) && sl.getSong().getId().equals(s3.getId()));
        assertThat(userHasKeptLike).isTrue();

        long otherUserLikesCount = remainingLikes.stream()
                .filter(sl -> sl.getUser().getId().equals(otherUser.getId()))
                .count();
        assertThat(otherUserLikesCount).isEqualTo(2);
    }
}