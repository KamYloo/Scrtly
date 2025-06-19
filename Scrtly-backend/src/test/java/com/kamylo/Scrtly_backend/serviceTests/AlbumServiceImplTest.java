package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.album.mapper.AlbumMapperImpl;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.song.mapper.SongMapperImpl;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.album.service.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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

    private Mapper<AlbumEntity, AlbumDto> albumMapper;
    private Mapper<SongEntity, SongDto> songMapper;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.albumMapper = new AlbumMapperImpl(new ModelMapper());
        this.songMapper = new SongMapperImpl(new ModelMapper());
        albumService = new AlbumServiceImpl(
                albumRepository,
                userService,
                artistRepository,
                userRoleService,
                fileService,
                albumMapper,
                songMapper,
                songRepository
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

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(albumImage.isEmpty()).thenReturn(false);
        when(fileService.saveFile(albumImage, "albumImages/")).thenReturn("path/to/image");
        when(albumRepository.save(any(AlbumEntity.class))).thenReturn(albumEntity);

        AlbumDto result = albumService.createAlbum(title, albumImage, username);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
    }

    @Test
    void getAlbums_shouldReturnAlbums() {
        Pageable pageable = PageRequest.of(0, 10);
        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setSongs(Collections.emptyList());
        Page<AlbumEntity> albumPage = new PageImpl<>(Collections.singletonList(albumEntity));

        when(albumRepository.findAll(pageable)).thenReturn(albumPage);

        Page<AlbumDto> result = albumService.getAlbums(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
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

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumEntity));

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
        albumEntity.setSongs(Collections.emptyList());
        Page<AlbumEntity> albumPage = new PageImpl<>(Collections.singletonList(albumEntity));

        when(albumRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(albumPage);

        Page<AlbumDto> result = albumService.searchAlbums(artistName, albumName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAlbumTracks_shouldReturnTracks() {
        int albumId = 1;
        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(albumId);
        albumEntity.setSongs(Collections.emptyList());

        SongEntity songEntity = new SongEntity();
        songEntity.setDuration(200);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumEntity));
        when(songRepository.findByAlbumId(albumEntity.getId())).thenReturn(Collections.singletonList(songEntity));

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

        Mapper<AlbumEntity, AlbumDto> albumMapperMock = mock(Mapper.class);
        when(albumMapperMock.mapFrom(any(AlbumDto.class))).thenReturn(null);

        AlbumServiceImpl serviceWithMockMapper = new AlbumServiceImpl(
                albumRepository,
                userService,
                artistRepository,
                userRoleService,
                fileService,
                albumMapperMock,
                songMapper,
                songRepository
        );

        CustomException exception = assertThrows(CustomException.class, () -> serviceWithMockMapper.getAlbumTracks(albumId));
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

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        when(albumRepository.findByArtistId(artistEntity.getId())).thenReturn(List.of(albumEntity));

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
        albumEntity.setArtist(user);

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
        albumEntity.setArtist(user);
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
        albumEntity.setArtist(otherUser);

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
