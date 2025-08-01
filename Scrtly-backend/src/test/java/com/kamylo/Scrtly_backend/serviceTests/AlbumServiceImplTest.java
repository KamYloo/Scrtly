package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.album.mapper.AlbumMapper;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.album.service.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private FileService fileService;

    @Mock
    private SongRepository songRepository;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private SongMapper songMapper;

    @Mock
    private HlsService hlsService;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        albumService = new AlbumServiceImpl(
                albumRepository,
                userService,
                artistRepository,
                userRoleService,
                fileService,
                albumMapper,
                songMapper,
                songRepository,
                hlsService
        );
    }

    @Test
    void createAlbum_shouldThrowException_whenUserIsNotArtist() {
        String username = "user@example.com";
        when(userRoleService.isArtist(username)).thenReturn(false);

        assertThrows(CustomException.class,
                () -> albumService.createAlbum("Test Album", mock(MultipartFile.class), username));
    }

    @Test
    void createAlbum_shouldCreateAlbum_whenUserIsArtist() {
        String title = "Test Album";
        MultipartFile albumImage = mock(MultipartFile.class);
        String username = "artist@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);
        ArtistEntity artist = new ArtistEntity();
        artist.setId(1L);
        user.setArtistEntity(artist);

        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(1);
        albumEntity.setTitle(title);
        albumEntity.setSongs(Collections.emptyList());

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(1);
        albumDto.setTitle(title);
        albumDto.setTracksCount(0);
        albumDto.setTotalDuration(0);

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(albumImage.isEmpty()).thenReturn(false);
        when(fileService.saveFile(albumImage, "albumImages/")).thenReturn("path/to/image");
        when(albumRepository.save(any(AlbumEntity.class))).thenReturn(albumEntity);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(albumDto);

        AlbumDto result = albumService.createAlbum(title, albumImage, username);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
    }

    @Test
    void getAlbums_shouldReturnAlbums() {
        Pageable pageable = PageRequest.of(0, 10);
        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(1);
        albumEntity.setTitle("Test Album");
        albumEntity.setSongs(Collections.emptyList());

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(1);
        albumDto.setTitle("Test Album");
        albumDto.setTracksCount(0);
        albumDto.setTotalDuration(0);

        Page<AlbumEntity> albumPage = new PageImpl<>(Collections.singletonList(albumEntity));

        when(albumRepository.findAll(pageable)).thenReturn(albumPage);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(albumDto);

        Page<AlbumDto> result = albumService.getAlbums(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Album", result.getContent().get(0).getTitle());
    }

    @Test
    void getAlbum_shouldThrowException_whenAlbumDoesNotExist() {
        int albumId = 1;
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> albumService.getAlbum(albumId));
    }

    @Test
    void getAlbum_shouldReturnAlbum_whenAlbumExists() {
        Integer albumId = 1;
        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(albumId);
        albumEntity.setTitle("Test Album");
        albumEntity.setSongs(Collections.emptyList());

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(albumId);
        albumDto.setTitle("Test Album");
        albumDto.setTracksCount(0);
        albumDto.setTotalDuration(0);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumEntity));
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(albumDto);

        AlbumDto result = albumService.getAlbum(albumId);

        assertNotNull(result);
        assertEquals(albumId, result.getId());
        assertEquals("Test Album", result.getTitle());
        assertEquals(0, result.getTracksCount());
        assertEquals(0, result.getTotalDuration());
    }

    @Test
    void searchAlbums_shouldReturnAlbums() {
        String artistName = "Artist";
        String albumName = "Album";
        Pageable pageable = PageRequest.of(0, 10);

        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(1);
        albumEntity.setTitle("Test Album");
        albumEntity.setSongs(Collections.emptyList());

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(1);
        albumDto.setTitle("Test Album");
        albumDto.setTracksCount(0);
        albumDto.setTotalDuration(0);

        Page<AlbumEntity> albumPage = new PageImpl<>(Collections.singletonList(albumEntity));

        when(albumRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(albumPage);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(albumDto);

        Page<AlbumDto> result = albumService.searchAlbums(artistName, albumName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Album", result.getContent().get(0).getTitle());
    }

    @Test
    void getAlbumTracks_shouldReturnTracks() {
        int albumId = 1;
        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(albumId);
        albumEntity.setSongs(Collections.emptyList());

        SongEntity songEntity = new SongEntity();
        songEntity.setDuration(200);

        SongDto songDto = new SongDto();
        songDto.setDuration(200);

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(albumId);
        albumDto.setTitle("Test Album");
        albumDto.setTracksCount(0);
        albumDto.setTotalDuration(0);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumEntity));
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(albumDto);
        when(albumMapper.toEntity(any(AlbumDto.class))).thenReturn(albumEntity);
        when(songRepository.findByAlbumId(albumEntity.getId())).thenReturn(Collections.singletonList(songEntity));
        when(songMapper.toDto(any(SongEntity.class))).thenReturn(songDto);

        List<SongDto> result = albumService.getAlbumTracks(albumId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(200, result.get(0).getDuration());
    }

    @Test
    void getAlbumTracks_shouldThrowException_whenAlbumMapperReturnsNull() {
        int albumId = 999;
        AlbumEntity fakeEntity = new AlbumEntity();
        fakeEntity.setId(albumId);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(fakeEntity));
        // AlbumMapper zwraca null
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> albumService.getAlbumTracks(albumId));
        assertEquals(BusinessErrorCodes.ALBUM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getAlbumsByArtist_shouldReturnAlbums() {
        Long artistId = 1L;
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(artistId);

        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(10);
        albumEntity.setTitle("Test Album");
        albumEntity.setSongs(Collections.emptyList());

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(10);
        albumDto.setTitle("Test Album");
        albumDto.setTracksCount(0);
        albumDto.setTotalDuration(0);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        when(albumRepository.findByArtistId(artistEntity.getId())).thenReturn(List.of(albumEntity));
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getAlbumsByArtist(artistId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Album", result.get(0).getTitle());
    }

    @Test
    void deleteAlbum_shouldDeleteAlbum_whenUserIsArtist() {
        int albumId = 1;
        String username = "artist@example.com";

        UserEntity user = new UserEntity();
        user.setId(1L);
        ArtistEntity artist = new ArtistEntity();
        artist.setId(1L);
        user.setArtistEntity(artist);

        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setArtist(user.getArtistEntity());

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumEntity));
        when(userService.findUserByEmail(username)).thenReturn(user);

        albumService.deleteAlbum(albumId, username);

        verify(albumRepository).deleteById(albumId);
    }

    @Test
    void deleteAlbum_shouldThrowException_whenUserIsNotArtist() {
        int albumId = 1;
        String username = "user@example.com";

        when(userRoleService.isArtist(username)).thenReturn(false);

        assertThrows(CustomException.class, () -> albumService.deleteAlbum(albumId, username));
    }

    @Test
    void deleteAlbum_shouldDeleteAlbumAndRelatedFiles_whenAlbumHasSongs() {
        int albumId = 1;
        String username = "artist@example.com";

        UserEntity user = new UserEntity();
        user.setId(1L);
        ArtistEntity artist = new ArtistEntity();
        artist.setId(1L);
        user.setArtistEntity(artist);

        SongEntity song1 = new SongEntity();
        song1.setTrack("track1.mp3");
        song1.setImageSong("cover1.jpg");

        SongEntity song2 = new SongEntity();
        song2.setTrack("track2.mp3");
        song2.setImageSong("cover2.jpg");

        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setArtist(user.getArtistEntity());
        albumEntity.setSongs(List.of(song1, song2));
        albumEntity.setCoverImage("coverAlbum.jpg");

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumEntity));
        when(userService.findUserByEmail(username)).thenReturn(user);

        albumService.deleteAlbum(albumId, username);

        verify(fileService).deleteFile("track1.mp3");
        verify(fileService).deleteFile("cover1.jpg");
        verify(fileService).deleteFile("track2.mp3");
        verify(fileService).deleteFile("cover2.jpg");
        verify(fileService).deleteFile("coverAlbum.jpg");

        verify(albumRepository).deleteById(albumId);
    }

    @Test
    void deleteAlbum_shouldThrowException_whenArtistDoesNotMatchAlbumArtist() {
        int albumId = 1;
        String username = "artist@example.com";

        UserEntity user = new UserEntity();
        user.setId(1L);
        ArtistEntity userArtist = new ArtistEntity();
        userArtist.setId(1L);
        user.setArtistEntity(userArtist);

        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);
        ArtistEntity albumArtist = new ArtistEntity();
        albumArtist.setId(2L);
        otherUser.setArtistEntity(albumArtist);

        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setArtist(otherUser.getArtistEntity());

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumEntity));
        when(userService.findUserByEmail(username)).thenReturn(user);

        assertThrows(CustomException.class, () -> albumService.deleteAlbum(albumId, username));
    }

    @Test
    void getAlbumsByArtist_shouldThrowException_whenArtistNotFound() {
        Long artistId = 999L;
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> albumService.getAlbumsByArtist(artistId));
        assertEquals(BusinessErrorCodes.ARTIST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getAlbum_shouldThrowException_whenAlbumNotFound() {
        int albumId = 1;
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> albumService.getAlbum(albumId));
        assertEquals(BusinessErrorCodes.ALBUM_NOT_FOUND, exception.getErrorCode());
    }

}
