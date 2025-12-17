package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.playList.mapper.PlayListMapper;
import com.kamylo.Scrtly_backend.playList.web.dto.PlayListDto;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.playList.domain.PlayListEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.playList.repository.PlayListRepository;
import com.kamylo.Scrtly_backend.like.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.common.service.impl.FileServiceImpl;
import com.kamylo.Scrtly_backend.playList.service.PlayListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PlayListServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long OTHER_ID = 2L;
    private static final String USER_EMAIL = "user@example.com";
    private static final String OTHER_EMAIL = "other@example.com";
    private static final Integer PLAYLIST_ID = 1;
    private static final Long SONG_ID = 100L;

    @Mock private UserService userService;
    @Mock private FileServiceImpl fileService;
    @Mock private PlayListRepository playListRepository;
    @Mock private SongRepository songRepository;
    @Mock private SongLikeRepository songLikeRepository;
    @Mock private PlayListMapper playListMapper;
    @Mock private SongMapper songMapper;

    @InjectMocks private PlayListServiceImpl playListService;

    private UserEntity user;
    private UserEntity anotherUser;
    private PlayListEntity playList;
    private MultipartFile playListImage;
    private SongEntity song;

    private UserEntity makeUser(Long id, String email) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setEmail(email);
        return u;
    }

    private PlayListEntity makePlayList(UserEntity owner) {
        return PlayListEntity.builder()
                .id(PlayListServiceImplTest.PLAYLIST_ID)
                .title("My Playlist")
                .user(owner)
                .coverImage("old/path")
                .songs(new HashSet<>())
                .favourite(false)
                .build();
    }

    private SongEntity makeSong() {
        return SongEntity.builder().id(PlayListServiceImplTest.SONG_ID).title("Song 1").build();
    }

    @BeforeEach
    void setUp() {
        user = makeUser(USER_ID, USER_EMAIL);
        anotherUser = makeUser(OTHER_ID, OTHER_EMAIL);
        playList = makePlayList(user);
        playListImage = mock(MultipartFile.class);
        song = makeSong();

        Mockito.lenient().when(playListRepository.save(any(PlayListEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Mockito.lenient().when(playListMapper.toDto(any(PlayListEntity.class)))
                .thenAnswer(inv -> {
                    PlayListEntity p = inv.getArgument(0);
                    PlayListDto d = new PlayListDto();
                    d.setTitle(p.getTitle());
                    d.setCoverImage(p.getCoverImage());
                    return d;
                });

        Mockito.lenient().when(songMapper.toDto(any(SongEntity.class)))
                .thenAnswer(inv -> {
                    SongEntity s = inv.getArgument(0);
                    SongDto sd = new SongDto();
                    sd.setId(s.getId());
                    sd.setTitle(s.getTitle());
                    return sd;
                });
    }

    @Nested
    class CreateTests {
        @Test
        void createPlayList_shouldCreatePlayList_withImage() {
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
            when(playListImage.isEmpty()).thenReturn(false);
            when(fileService.saveFile(playListImage, "playListImages/")).thenReturn("new/image/path");

            PlayListDto result = playListService.createPlayList("Test Playlist", USER_EMAIL, playListImage);

            assertNotNull(result);
            assertEquals("Test Playlist", result.getTitle());
            assertEquals("new/image/path", result.getCoverImage());

            verify(fileService).saveFile(playListImage, "playListImages/");
            verify(playListRepository).save(any(PlayListEntity.class));
            verify(playListMapper).toDto(any(PlayListEntity.class));
        }

        @Test
        void createPlayList_shouldCreatePlayList_withoutImage() {
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
            when(playListImage.isEmpty()).thenReturn(true);

            PlayListDto result = playListService.createPlayList("Test Playlist", USER_EMAIL, playListImage);

            assertNotNull(result);
            assertEquals("Test Playlist", result.getTitle());
            assertNull(result.getCoverImage());

            verify(fileService, never()).saveFile(any(), anyString());
            verify(playListRepository).save(any(PlayListEntity.class));
            verify(playListMapper).toDto(any(PlayListEntity.class));
        }
    }

    @Nested
    class GetTests {
        @Test
        void getPlayList_shouldReturnPlayList() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));

            PlayListDto result = playListService.getPlayList(PLAYLIST_ID);

            assertNotNull(result);
            assertEquals("My Playlist", result.getTitle());
            assertEquals("old/path", result.getCoverImage());

            verify(playListRepository).findById(PLAYLIST_ID);
            verify(playListMapper).toDto(playList);
        }

        @Test
        void getPlayList_shouldThrowException_whenNotFound() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class, () -> playListService.getPlayList(PLAYLIST_ID));
            assertEquals(BusinessErrorCodes.PLAYLIST_NOT_FOUND, ex.getErrorCode());

            verify(playListRepository).findById(PLAYLIST_ID);
            verifyNoInteractions(playListMapper);
        }
    }

    @Test
    void getPlayLists_shouldReturnPagedPlayLists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlayListEntity> page = new PageImpl<>(List.of(playList));
        when(playListRepository.findAll(pageable)).thenReturn(page);

        Page<PlayListDto> result = playListService.getPlayLists(pageable);

        List<PlayListDto> content = result.getContent();
        assertNotNull(content);
        assertEquals(1, content.size());
        PlayListDto dto = content.getFirst();
        assertEquals("My Playlist", dto.getTitle());
        assertEquals("old/path", dto.getCoverImage());

        verify(playListRepository).findAll(pageable);
        verify(playListMapper).toDto(any(PlayListEntity.class));
    }

    @Test
    void getPlayListsByUser_shouldReturnUserPlayLists() {
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlayListEntity> page = new PageImpl<>(List.of(playList));
        when(playListRepository.getPlayListsByUserId(user.getId(), pageable)).thenReturn(page);

        Page<PlayListDto> result = playListService.getPlayListsByUser(USER_EMAIL, pageable);

        List<PlayListDto> content = result.getContent();
        assertNotNull(content);
        assertEquals(1, content.size());
        PlayListDto dto = content.getFirst();
        assertEquals("My Playlist", dto.getTitle());
        assertEquals("old/path", dto.getCoverImage());

        verify(userService).findUserByEmail(USER_EMAIL);
        verify(playListRepository).getPlayListsByUserId(user.getId(), pageable);
        verify(playListMapper).toDto(any(PlayListEntity.class));
    }

    @Nested
    class AddSongTests {
        @Test
        void addSongToPlayList_shouldAddSong_whenNotAlreadyPresent_andOwnerValid() {
            when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            playList.getSongs().clear();
            PlayListDto result = playListService.addSongToPlayList(SONG_ID, PLAYLIST_ID, USER_EMAIL);

            assertNotNull(result);
            verify(songRepository).findById(SONG_ID);
            verify(playListRepository).findById(PLAYLIST_ID);
            verify(playListRepository).save(playList);
            assertTrue(playList.getSongs().stream().anyMatch(s -> s.getId().equals(SONG_ID)));
        }

        @Test
        void addSongToPlayList_shouldThrowSongExists_whenSongAlreadyInPlayList() {
            when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
            playList.getSongs().add(song);
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            CustomException ex = assertThrows(CustomException.class, () ->
                    playListService.addSongToPlayList(SONG_ID, PLAYLIST_ID, USER_EMAIL)
            );
            assertEquals(BusinessErrorCodes.SONG_EXISTS, ex.getErrorCode());
            verify(playListRepository, never()).save(any());
        }

        @Test
        void addSongToPlayList_shouldThrowPlaylistMismatch_whenOwnershipInvalid() {
            when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(OTHER_EMAIL)).thenReturn(anotherUser);

            CustomException ex = assertThrows(CustomException.class, () ->
                    playListService.addSongToPlayList(SONG_ID, PLAYLIST_ID, OTHER_EMAIL)
            );
            assertEquals(BusinessErrorCodes.PLAYLIST_MISMATCH, ex.getErrorCode());
            verify(playListRepository, never()).save(any());
        }

        @Test
        void addSongToPlayList_shouldThrowPlaylistNotFound_whenPlaylistDoesNotExist() {
            when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class,
                    () -> playListService.addSongToPlayList(SONG_ID, PLAYLIST_ID, USER_EMAIL));
            assertEquals(BusinessErrorCodes.PLAYLIST_NOT_FOUND, ex.getErrorCode());
            verify(playListRepository, never()).save(any());
        }

        @Test
        void addSongToPlayList_shouldThrowSongNotFound_whenSongDoesNotExist() {
            when(songRepository.findById(SONG_ID)).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class, () ->
                    playListService.addSongToPlayList(SONG_ID, PLAYLIST_ID, USER_EMAIL)
            );
            assertEquals(BusinessErrorCodes.SONG_NOT_FOUND, ex.getErrorCode());
            verify(playListRepository, never()).save(any());
        }
    }

    @Test
    void addToFavourites_shouldAddSong_ifNotPresent() {
        PlayListEntity fav = PlayListEntity.builder()
                .user(user)
                .favourite(true)
                .title("Favourite Songs")
                .songs(new HashSet<>())
                .build();
        when(playListRepository.findByUserIdAndFavourite(user.getId(), true))
                .thenReturn(Optional.of(fav));

        playListService.addToFavourites(user, song);
        assertTrue(fav.getSongs().contains(song));
        verify(playListRepository, times(1)).save(fav);
    }

    @Test
    void addToFavourites_shouldNotAddSong_ifAlreadyPresent() {
        PlayListEntity fav = PlayListEntity.builder()
                .user(user)
                .favourite(true)
                .title("Favourite Songs")
                .songs(new HashSet<>(Set.of(song)))
                .build();
        when(playListRepository.findByUserIdAndFavourite(user.getId(), true))
                .thenReturn(Optional.of(fav));

        playListService.addToFavourites(user, song);

        verify(playListRepository, never()).save(fav);
        assertTrue(fav.getSongs().contains(song));
    }

    @Test
    void addToFavourites_shouldCreateFavouritePlayList_whenNotExists() {
        when(playListRepository.findByUserIdAndFavourite(user.getId(), true))
                .thenReturn(Optional.empty());

        playListService.addToFavourites(user, song);

        ArgumentCaptor<PlayListEntity> captor = ArgumentCaptor.forClass(PlayListEntity.class);
        verify(playListRepository, times(2)).save(captor.capture());

        PlayListEntity created = captor.getAllValues().getFirst();
        assertTrue(created.isFavourite());
        assertEquals(user.getId(), created.getUser().getId());
    }

    @Test
    void removeFromFavourites_shouldRemoveSong() {
        PlayListEntity fav = PlayListEntity.builder()
                .user(user)
                .favourite(true)
                .title("Favourite Songs")
                .songs(new HashSet<>(Set.of(song)))
                .build();
        when(playListRepository.findByUserIdAndFavourite(user.getId(), true))
                .thenReturn(Optional.of(fav));

        playListService.removeFromFavourites(user, song);
        assertFalse(fav.getSongs().contains(song));
        verify(playListRepository, times(1)).save(fav);
    }

    @Test
    void removeSongFromPlayList_shouldRemoveSong_whenPresent_andOwnershipValid() {
        when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
        playList.getSongs().add(song);
        when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        playListService.removeSongFromPlayList(SONG_ID, PLAYLIST_ID, USER_EMAIL);
        assertFalse(playList.getSongs().contains(song));
        verify(playListRepository, times(1)).save(playList);
    }

    @Test
    void removeSongFromPlayList_shouldThrowSongNotExists_whenSongNotInPlayList() {
        when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
        when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.removeSongFromPlayList(SONG_ID, PLAYLIST_ID, USER_EMAIL)
        );
        assertEquals(BusinessErrorCodes.SONG_NOT_EXISTS, ex.getErrorCode());
        verify(playListRepository, never()).save(any());
    }

    @Test
    void removeSongFromPlayList_shouldThrowPlaylistMismatch_whenOwnershipInvalid() {
        when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
        when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail(OTHER_EMAIL)).thenReturn(anotherUser);

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.removeSongFromPlayList(SONG_ID, PLAYLIST_ID, OTHER_EMAIL)
        );
        assertEquals(BusinessErrorCodes.PLAYLIST_MISMATCH, ex.getErrorCode());
        verify(playListRepository, never()).save(any());
    }

    @Test
    void removeSongFromPlayList_shouldThrowPlaylistNotFound_whenPlaylistDoesNotExist() {
        when(songRepository.findById(SONG_ID)).thenReturn(Optional.of(song));
        when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> playListService.removeSongFromPlayList(SONG_ID, PLAYLIST_ID, USER_EMAIL));
        assertEquals(BusinessErrorCodes.PLAYLIST_NOT_FOUND, ex.getErrorCode());
        verify(playListRepository, never()).save(any());
    }

    @Test
    void getPlayListTracks_shouldReturnSongDtos_andCheckFavorites_whenUserLoggedIn() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SongEntity> page = new PageImpl<>(List.of(song));

        when(songRepository.findByPlaylistId(PLAYLIST_ID, pageable)).thenReturn(page);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
        when(songLikeRepository.findSongIdsLikedByUser(eq(user.getId()), anyList()))
                .thenReturn(Set.of(song.getId()));

        Page<SongDto> result = playListService.getPlayListTracks(PLAYLIST_ID, pageable, USER_EMAIL);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        SongDto dto = result.getContent().getFirst();
        assertEquals("Song 1", dto.getTitle());
        assertTrue(dto.isFavorite());

        verify(songRepository).findByPlaylistId(PLAYLIST_ID, pageable);
        verify(userService).findUserByEmail(USER_EMAIL);
        verify(songLikeRepository).findSongIdsLikedByUser(eq(user.getId()), anyList());
        verify(songMapper).toDto(any(SongEntity.class));
    }

    @Test
    void getPlayListTracks_shouldReturnSongDtos_withoutFavorites_whenUserNotLoggedIn() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SongEntity> page = new PageImpl<>(List.of(song));

        when(songRepository.findByPlaylistId(PLAYLIST_ID, pageable)).thenReturn(page);

        Page<SongDto> result = playListService.getPlayListTracks(PLAYLIST_ID, pageable, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        SongDto dto = result.getContent().getFirst();
        assertEquals("Song 1", dto.getTitle());
        assertFalse(dto.isFavorite());

        verify(songRepository).findByPlaylistId(PLAYLIST_ID, pageable);
        verify(userService, never()).findUserByEmail(anyString());
        verify(songLikeRepository, never()).findSongIdsLikedByUser(anyLong(), anyList());
        verify(songMapper).toDto(any(SongEntity.class));
    }

    @Test
    void getPlayListTracks_shouldReturnEmptyPage_whenNoSongsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(songRepository.findByPlaylistId(PLAYLIST_ID, pageable)).thenReturn(Page.empty(pageable));

        Page<SongDto> result = playListService.getPlayListTracks(PLAYLIST_ID, pageable, USER_EMAIL);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(songRepository).findByPlaylistId(PLAYLIST_ID, pageable);
        verify(userService, never()).findUserByEmail(anyString());
        verify(songLikeRepository, never()).findSongIdsLikedByUser(anyLong(), anyList());
    }

    @Nested
    class UpdateTests {
        @Test
        void updatePlayList_shouldUpdateTitleAndImage_whenProvided() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
            when(playListImage.isEmpty()).thenReturn(false);
            when(fileService.updateFile(playListImage, "old/path", "playListImages/")).thenReturn("new/image/path");

            PlayListDto result = playListService.updatePlayList(PLAYLIST_ID, "New Title", USER_EMAIL, playListImage);

            assertNotNull(result);
            assertEquals("New Title", result.getTitle());
            assertEquals("new/image/path", result.getCoverImage());
            verify(fileService).updateFile(playListImage, "old/path", "playListImages/");
            verify(playListRepository).save(playList);
        }

        @Test
        void updatePlayList_shouldUpdateOnlyTitle_whenImageNullOrEmpty() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            PlayListDto result = playListService.updatePlayList(PLAYLIST_ID, "New Title", USER_EMAIL, null);

            assertNotNull(result);
            assertEquals("New Title", result.getTitle());
            assertEquals("old/path", result.getCoverImage());
            verify(playListRepository).save(playList);
        }

        @Test
        void updatePlayList_shouldNotUpdateTitle_whenTitleIsEmpty() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            PlayListDto result = playListService.updatePlayList(PLAYLIST_ID, "", USER_EMAIL, null);

            assertNotNull(result);
            assertEquals("My Playlist", result.getTitle());
            verify(playListRepository).save(playList);
        }

        @Test
        void updatePlayList_shouldNotUpdateImage_whenImageIsEmpty() {
            when(playListImage.isEmpty()).thenReturn(true);
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            PlayListDto result = playListService.updatePlayList(PLAYLIST_ID, "New Title", USER_EMAIL, playListImage);

            verify(fileService, never()).updateFile(any(), anyString(), anyString());
            assertEquals("old/path", result.getCoverImage());
        }

        @Test
        void updatePlayList_shouldThrowPlaylistNotFound_whenPlayListMissing() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class,
                    () -> playListService.updatePlayList(PLAYLIST_ID, "Title", USER_EMAIL, null));
            assertEquals(BusinessErrorCodes.PLAYLIST_NOT_FOUND, ex.getErrorCode());
            verify(playListRepository, never()).save(any());
        }

        @Test
        void updatePlayList_shouldThrowPlaylistMismatch_whenOwnershipInvalid() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(OTHER_EMAIL)).thenReturn(anotherUser);

            CustomException ex = assertThrows(CustomException.class,
                    () -> playListService.updatePlayList(PLAYLIST_ID, "New Title", OTHER_EMAIL, null));
            assertEquals(BusinessErrorCodes.PLAYLIST_MISMATCH, ex.getErrorCode());
            verify(playListRepository, never()).save(any());
            verify(fileService, never()).updateFile(any(), anyString(), anyString());
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void deletePlayList_shouldDeletePlayList_whenNotFavourite_andOwnershipValid() {
            playList.setFavourite(false);
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            playListService.deletePlayList(PLAYLIST_ID, USER_EMAIL);

            verify(playListRepository).delete(playList);
            verify(fileService).deleteFile(playList.getCoverImage());
        }

        @Test
        void deletePlayList_shouldProcessFavourite_whenFavouriteTrue() {
            playList.setFavourite(true);
            playList.getSongs().add(song);
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            playListService.deletePlayList(PLAYLIST_ID, USER_EMAIL);

            verify(songLikeRepository).deleteBySong(song);
            verify(playListRepository).delete(playList);
            verify(fileService).deleteFile(playList.getCoverImage());
        }

        @Test
        void deletePlayList_shouldThrowPlaylistMismatch_whenOwnershipInvalid() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.of(playList));
            when(userService.findUserByEmail(OTHER_EMAIL)).thenReturn(anotherUser);

            CustomException ex = assertThrows(CustomException.class,
                    () -> playListService.deletePlayList(PLAYLIST_ID, OTHER_EMAIL));
            assertEquals(BusinessErrorCodes.PLAYLIST_MISMATCH, ex.getErrorCode());
            verify(playListRepository, never()).delete(any());
            verify(fileService, never()).deleteFile(anyString());
        }

        @Test
        void deletePlayList_shouldThrowPlaylistNotFound_whenPlayListMissing() {
            when(playListRepository.findById(PLAYLIST_ID)).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class,
                    () -> playListService.deletePlayList(PLAYLIST_ID, USER_EMAIL));
            assertEquals(BusinessErrorCodes.PLAYLIST_NOT_FOUND, ex.getErrorCode());
            verify(playListRepository, never()).delete(any());
        }
    }
}