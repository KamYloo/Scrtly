package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.album.mapper.AlbumMapper;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.album.service.AlbumServiceImpl;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.like.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    private static final String ARTIST_EMAIL = "artist@example.com";
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String USER_EMAIL = "user@example.com";
    private static final String ALBUM_IMAGE_PATH = "albumImages/";
    private static final String SAVED_IMAGE_PATH = "path/to/image.jpg";

    @Mock private AlbumRepository albumRepository;
    @Mock private UserService userService;
    @Mock private UserRoleService userRoleService;
    @Mock private FileService fileService;
    @Mock private SongRepository songRepository;
    @Mock private AlbumMapper albumMapper;
    @Mock private SongMapper songMapper;
    @Mock private HlsService hlsService;
    @Mock private SongLikeRepository songLikeRepository;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @BeforeEach
    void setUp() {
    }

    private UserEntity createUserWithArtist(long userId, Integer artistId) {
        UserEntity u = new UserEntity();
        u.setId(userId);
        if (artistId != null) {
            ArtistEntity a = new ArtistEntity();
            a.setId(artistId.longValue());
            u.setArtistEntity(a);
        }
        return u;
    }

    private SongEntity createSong(Integer id, String track, String imageSong, int duration) {
        SongEntity s = new SongEntity();
        s.setId(Long.valueOf(id));
        s.setTrack(track);
        s.setImageSong(imageSong);
        s.setDuration(duration);
        return s;
    }

    private AlbumEntity createAlbumEntity(Integer id, Long artistId, List<SongEntity> songs, String coverImage) {
        AlbumEntity alb = new AlbumEntity();
        alb.setId(id);
        if (artistId != null) {
            ArtistEntity a = new ArtistEntity();
            a.setId(artistId);
            alb.setArtist(a);
        }
        alb.setSongs(songs != null ? songs : Collections.emptyList());
        alb.setCoverImage(coverImage);
        alb.setTitle("Title-" + id);
        return alb;
    }

    private AlbumDto createAlbumDto(Integer id, String title) {
        AlbumDto dto = new AlbumDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setTracksCount(0);
        dto.setTotalDuration(0);
        return dto;
    }

    private void stubRoles(String username, boolean isArtist, boolean isAdmin) {
        lenient().when(userRoleService.isArtist(username)).thenReturn(isArtist);
        lenient().when(userRoleService.isAdmin(username)).thenReturn(isAdmin);
    }

    private void stubFindUser(String username, UserEntity user) {
        when(userService.findUserByEmail(username)).thenReturn(user);
    }

    private void stubFindAlbumById(Integer albumId, AlbumEntity album) {
        when(albumRepository.findById(albumId)).thenReturn(Optional.ofNullable(album));
    }

    @Test
    void createAlbum_shouldThrow_whenNotArtistNorAdmin() {
        MultipartFile mockFile = mock(MultipartFile.class);
        stubRoles(USER_EMAIL, false, false);

        CustomException ex = assertThrows(CustomException.class,
                () -> albumService.createAlbum("Title", mockFile, USER_EMAIL));
        assertEquals(BusinessErrorCodes.ARTIST_UNAUTHORIZED, ex.getErrorCode());

        verifyNoInteractions(userService, albumRepository, fileService, albumMapper);
    }

    @Test
    void createAlbum_shouldSaveWithImage_whenArtistAndImageProvided() {
        String title = "My Album";
        MultipartFile file = mock(MultipartFile.class);

        stubRoles(ARTIST_EMAIL, true, false);
        UserEntity user = createUserWithArtist(1L, 1);
        stubFindUser(ARTIST_EMAIL, user);

        when(file.isEmpty()).thenReturn(false);
        when(fileService.saveFile(file, ALBUM_IMAGE_PATH)).thenReturn(SAVED_IMAGE_PATH);

        AlbumEntity savedEntity = createAlbumEntity(1, 1L, Collections.emptyList(), SAVED_IMAGE_PATH);
        AlbumDto dto = createAlbumDto(1, title);

        when(albumRepository.save(any(AlbumEntity.class))).thenReturn(savedEntity);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(dto);

        AlbumDto result = albumService.createAlbum(title, file, ARTIST_EMAIL);

        assertNotNull(result);
        assertEquals(title, result.getTitle());

        ArgumentCaptor<AlbumEntity> captor = ArgumentCaptor.forClass(AlbumEntity.class);
        verify(albumRepository).save(captor.capture());
        AlbumEntity passed = captor.getValue();
        assertEquals(SAVED_IMAGE_PATH, passed.getCoverImage());

        verify(fileService).saveFile(file, ALBUM_IMAGE_PATH);
        verify(albumMapper).toDto(savedEntity);
    }

    @Test
    void createAlbum_shouldAllow_whenAdminAndNoImage() {
        String title = "Admin Album";
        MultipartFile file = mock(MultipartFile.class);

        stubRoles(ADMIN_EMAIL, false, true);
        UserEntity adminUser = createUserWithArtist(10L, 10);
        stubFindUser(ADMIN_EMAIL, adminUser);

        when(file.isEmpty()).thenReturn(true);

        AlbumEntity savedEntity = createAlbumEntity(2, 10L, Collections.emptyList(), null);
        AlbumDto dto = createAlbumDto(2, title);

        when(albumRepository.save(any(AlbumEntity.class))).thenReturn(savedEntity);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(dto);

        AlbumDto result = albumService.createAlbum(title, file, ADMIN_EMAIL);

        assertNotNull(result);
        assertEquals(title, result.getTitle());

        verify(fileService, never()).saveFile(any(), anyString());
        verify(albumRepository).save(any(AlbumEntity.class));
    }

    @Test
    void getAlbums_returnsPagedDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        AlbumEntity entity = createAlbumEntity(1, 1L, Collections.emptyList(), null);
        AlbumDto dto = createAlbumDto(1, "Test");

        Page<AlbumEntity> page = new PageImpl<>(List.of(entity));
        when(albumRepository.findAll(pageable)).thenReturn(page);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(dto);

        Page<AlbumDto> result = albumService.getAlbums(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test", result.getContent().getFirst().getTitle());
    }

    @Test
    void searchAlbums_usesSpecification_andMaps() {
        Pageable pageable = PageRequest.of(0, 5);
        AlbumEntity entity = createAlbumEntity(3, 3L, Collections.emptyList(), null);
        AlbumDto dto = createAlbumDto(3, "S");

        Page<AlbumEntity> page = new PageImpl<>(List.of(entity));
        when(albumRepository.findAll(ArgumentMatchers.<Specification<AlbumEntity>>any(), eq(pageable)))
                .thenReturn(page);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(dto);

        Page<AlbumDto> result = albumService.searchAlbums("A", "B", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAlbum_throws_whenNotFound() {
        Integer id = 100;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> albumService.getAlbum(id));
        assertEquals(BusinessErrorCodes.ALBUM_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAlbum_returnsDto_whenFound() {
        Integer id = 5;
        AlbumEntity entity = createAlbumEntity(id, 2L, Collections.emptyList(), null);
        AlbumDto dto = createAlbumDto(id, "Found");

        when(albumRepository.findById(id)).thenReturn(Optional.of(entity));
        when(albumMapper.toDto(entity)).thenReturn(dto);

        AlbumDto r = albumService.getAlbum(id);
        assertEquals(id, r.getId());
        assertEquals("Found", r.getTitle());
    }

    @Test
    void getAlbumTracks_throws_whenAlbumNotFound() {
        Integer albumId = 999;
        String username = "user@example.com";

        when(albumRepository.existsById(albumId)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class,
                () -> albumService.getAlbumTracks(albumId, username));

        assertEquals(BusinessErrorCodes.ALBUM_NOT_FOUND, ex.getErrorCode());

        verify(songRepository, never()).findByAlbumId(anyInt());
    }

    @Test
    void getAlbumTracks_returnsTracksWithoutFavorites_whenUsernameIsNull() {
        Integer albumId = 10;
        SongEntity song = createSong(100, "track.mp3", "img.jpg", 300);
        SongDto songDto = new SongDto();
        songDto.setId(100L);
        songDto.setDuration(300);

        when(albumRepository.existsById(albumId)).thenReturn(true);
        when(songRepository.findByAlbumId(albumId)).thenReturn(List.of(song));
        when(songMapper.toDto(song)).thenReturn(songDto);

        List<SongDto> result = albumService.getAlbumTracks(albumId, null);

        assertEquals(1, result.size());
        assertEquals(300, result.getFirst().getDuration());
        assertFalse(result.getFirst().isFavorite());

        verify(userService, never()).findUserByEmail(anyString());
        verify(songLikeRepository, never()).findSongIdsLikedByUser(anyLong(), anyList());
    }

    @Test
    void getAlbumTracks_returnsTracksWithFavorites_whenUserLoggedIn() {
        Integer albumId = 20;
        String username = "fan@example.com";
        Long userId = 555L;

        SongEntity s1 = createSong(201, "t1.mp3", "i1.jpg", 200);
        SongEntity s2 = createSong(202, "t2.mp3", "i2.jpg", 210);

        SongDto d1 = new SongDto(); d1.setId(201L);
        SongDto d2 = new SongDto(); d2.setId(202L);

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(albumRepository.existsById(albumId)).thenReturn(true);
        when(songRepository.findByAlbumId(albumId)).thenReturn(List.of(s1, s2));
        when(userService.findUserByEmail(username)).thenReturn(user);

        when(songLikeRepository.findSongIdsLikedByUser(eq(userId), anyList()))
                .thenReturn(Set.of(201L));

        when(songMapper.toDto(s1)).thenReturn(d1);
        when(songMapper.toDto(s2)).thenReturn(d2);

        List<SongDto> result = albumService.getAlbumTracks(albumId, username);

        assertEquals(2, result.size());
        assertTrue(result.stream().filter(s -> s.getId().equals(201L)).findFirst().get().isFavorite());
        assertFalse(result.stream().filter(s -> s.getId().equals(202L)).findFirst().get().isFavorite());
    }

    @Test
    void getAlbumTracks_returnsEmptyList_whenNoSongsFound() {
        Integer albumId = 30;
        when(albumRepository.existsById(albumId)).thenReturn(true);
        when(songRepository.findByAlbumId(albumId)).thenReturn(Collections.emptyList());

        List<SongDto> result = albumService.getAlbumTracks(albumId, "anyUser");

        assertTrue(result.isEmpty());

        verify(userService, never()).findUserByEmail(anyString());
    }

    @Test
    void getAlbumsByArtist_paged_returnsPage_whenNoQuery() {
        Long artistId = 7L;
        Pageable pageable = PageRequest.of(0, 9);
        AlbumEntity entity = createAlbumEntity(77, artistId, Collections.emptyList(), null);
        AlbumDto dto = createAlbumDto(77, "PagedNoQuery");

        Page<AlbumEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(albumRepository.findByArtistId(eq(artistId), eq(pageable))).thenReturn(page);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(dto);

        Page<AlbumDto> result = albumService.getAlbumsByArtist(artistId, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("PagedNoQuery", result.getContent().getFirst().getTitle());
    }

    @Test
    void getAlbumsByArtist_paged_returnsFilteredPage_whenQueryProvided() {
        Long artistId = 8L;
        Pageable pageable = PageRequest.of(0, 9);
        AlbumEntity entity = createAlbumEntity(88, artistId, Collections.emptyList(), null);
        AlbumDto dto = createAlbumDto(88, "Filtered");

        Page<AlbumEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(albumRepository.findByArtistIdAndTitleIgnoreCaseContaining(eq(artistId), eq("Filtered"), eq(pageable)))
                .thenReturn(page);
        when(albumMapper.toDto(any(AlbumEntity.class))).thenReturn(dto);

        Page<AlbumDto> result = albumService.getAlbumsByArtist(artistId, "Filtered", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Filtered", result.getContent().getFirst().getTitle());
    }

    @Test
    void deleteAlbum_throws_whenNotArtistNorAdmin() {
        Integer albumId = 1;
        stubRoles(USER_EMAIL, false, false);

        CustomException ex = assertThrows(CustomException.class, () -> albumService.deleteAlbum(albumId, USER_EMAIL));
        assertEquals(BusinessErrorCodes.ARTIST_UNAUTHORIZED, ex.getErrorCode());
        verify(albumRepository, never()).findById(anyInt());
    }

    @Test
    void deleteAlbum_throws_whenAlbumMissing() {
        Integer albumId = 2;
        stubRoles(ARTIST_EMAIL, true, false);
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> albumService.deleteAlbum(albumId, ARTIST_EMAIL));
        assertEquals(BusinessErrorCodes.ALBUM_NOT_FOUND, ex.getErrorCode());
        verify(albumRepository, never()).deleteById(anyInt());
        verify(fileService, never()).deleteFile(anyString());
    }

    @Test
    void deleteAlbum_deletesFilesAndHls_whenArtistOwnsAlbum() {
        Integer albumId = 3;
        stubRoles(ARTIST_EMAIL, true, false);

        UserEntity user = createUserWithArtist(1L, 1);
        stubFindUser(ARTIST_EMAIL, user);

        SongEntity s1 = createSong(101, "t1.mp3", "i1.jpg", 100);
        SongEntity s2 = createSong(102, "t2.mp3", "i2.jpg", 120);
        AlbumEntity album = createAlbumEntity(albumId, 1L, List.of(s1, s2), "cover.jpg");

        stubFindAlbumById(albumId, album);

        albumService.deleteAlbum(albumId, ARTIST_EMAIL);

        verify(fileService).deleteFile("t1.mp3");
        verify(fileService).deleteFile("i1.jpg");
        verify(hlsService).deleteHlsFolder(101L);
        verify(fileService).deleteFile("t2.mp3");
        verify(fileService).deleteFile("i2.jpg");
        verify(hlsService).deleteHlsFolder(102L);
        verify(fileService).deleteFile("cover.jpg");
        verify(albumRepository).deleteById(albumId);
    }

    @Test
    void deleteAlbum_throws_whenArtistMismatch_andNotAdmin() {
        Integer albumId = 4;
        stubRoles(ARTIST_EMAIL, true, false);

        UserEntity user = createUserWithArtist(1L, 1);
        stubFindUser(ARTIST_EMAIL, user);

        AlbumEntity album = createAlbumEntity(albumId, 99L, Collections.emptyList(), null);
        stubFindAlbumById(albumId, album);

        CustomException ex = assertThrows(CustomException.class, () -> albumService.deleteAlbum(albumId, ARTIST_EMAIL));
        assertEquals(BusinessErrorCodes.ARTIST_MISMATCH, ex.getErrorCode());
        verify(albumRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteAlbum_allowsAdminEvenIfNotArtist() {
        Integer albumId = 5;
        stubRoles(ADMIN_EMAIL, false, true);

        UserEntity admin = createUserWithArtist(50L, 50);
        stubFindUser(ADMIN_EMAIL, admin);

        SongEntity s = createSong(999, "track.mp3", "img.jpg", 10);
        AlbumEntity album = createAlbumEntity(albumId, 200L, List.of(s), "albumCover.jpg");
        stubFindAlbumById(albumId, album);

        assertDoesNotThrow(() -> albumService.deleteAlbum(albumId, ADMIN_EMAIL));

        verify(fileService).deleteFile("track.mp3");
        verify(fileService).deleteFile("img.jpg");
        verify(hlsService).deleteHlsFolder(999L);
        verify(fileService).deleteFile("albumCover.jpg");
        verify(albumRepository).deleteById(albumId);
    }
}