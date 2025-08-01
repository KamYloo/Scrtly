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
@MockitoSettings(strictness = Strictness.LENIENT)
class PlayListServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private PlayListRepository playListRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private SongLikeRepository songLikeRepository;

    @Mock
    private PlayListMapper playListMapper;

    @Mock
    private SongMapper songMapper;

    private PlayListServiceImpl playListService;

    private UserEntity user;
    private UserEntity anotherUser;
    private PlayListEntity playList;
    private PlayListDto playListDto;
    private MultipartFile playListImage;

    private SongEntity song;
    private SongDto songDto;

    @BeforeEach
    void setUp() {
        playListService = new PlayListServiceImpl(
                userService,
                fileService,
                playListRepository,
                songRepository,
                songLikeRepository,
                playListMapper,
                songMapper
        );

        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        anotherUser = new UserEntity();
        anotherUser.setId(2L);
        anotherUser.setEmail("other@example.com");

        playList = PlayListEntity.builder()
                .id(1)
                .title("My Playlist")
                .user(user)
                .coverImage("old/path")
                .songs(new HashSet<>())
                .favourite(false)
                .build();

        playListDto = new PlayListDto();
        playListDto.setTitle("My Playlist");
        playListDto.setCoverImage("old/path");

        playListImage = mock(MultipartFile.class);

        song = SongEntity.builder()
                .id(100L)
                .title("Song 1")
                .build();
        songDto = new SongDto();
        songDto.setTitle("Song 1");
    }

    @Test
    void createPlayList_shouldCreatePlayList_withImage() {
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(playListImage.isEmpty()).thenReturn(false);
        when(fileService.saveFile(playListImage, "playListImages/")).thenReturn("new/image/path");

        PlayListEntity newPlaylist = PlayListEntity.builder()
                .title("Test Playlist")
                .user(user)
                .coverImage("new/image/path")
                .build();
        when(playListRepository.save(any())).thenReturn(newPlaylist);

        PlayListDto expectedDto = new PlayListDto();
        expectedDto.setTitle("Test Playlist");
        expectedDto.setCoverImage("new/image/path");
        when(playListMapper.toDto(any())).thenReturn(expectedDto);

        PlayListDto result = playListService.createPlayList("Test Playlist", "user@example.com", playListImage);

        assertNotNull(result, "DTO nie może być null");
        assertEquals("Test Playlist", result.getTitle());
        assertEquals("new/image/path", result.getCoverImage());
    }

    @Test
    void createPlayList_shouldCreatePlayList_withoutImage() {
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(playListImage.isEmpty()).thenReturn(true);

        PlayListEntity newPlaylist = PlayListEntity.builder()
                .title("Test Playlist")
                .user(user)
                .coverImage(null)
                .build();
        when(playListRepository.save(any())).thenReturn(newPlaylist);

        PlayListDto expectedDto = new PlayListDto();
        expectedDto.setTitle("Test Playlist");
        expectedDto.setCoverImage(null);
        when(playListMapper.toDto(any())).thenReturn(expectedDto);

        PlayListDto result = playListService.createPlayList("Test Playlist", "user@example.com", playListImage);

        assertNotNull(result, "DTO nie może być null");
        assertEquals("Test Playlist", result.getTitle());
        assertNull(result.getCoverImage());
    }

    @Test
    void getPlayList_shouldReturnPlayList() {
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(playListMapper.toDto(any())).thenReturn(playListDto);

        PlayListDto result = playListService.getPlayList(1);

        assertNotNull(result);
        assertEquals("My Playlist", result.getTitle());
        assertEquals("old/path", result.getCoverImage());
    }

    @Test
    void getPlayList_shouldThrowException_whenNotFound() {
        when(playListRepository.findById(1)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> playListService.getPlayList(1));
        assertEquals(BusinessErrorCodes.PLAYLIST_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getPlayLists_shouldReturnPagedPlayLists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlayListEntity> page = new PageImpl<>(List.of(playList));
        when(playListRepository.findAll(pageable)).thenReturn(page);
        when(playListMapper.toDto(any())).thenReturn(playListDto);

        Page<PlayListDto> result = playListService.getPlayLists(pageable);

        List<PlayListDto> content = result.getContent();
        assertNotNull(content);
        assertEquals(1, content.size());
        PlayListDto dto = content.get(0);
        assertEquals("My Playlist", dto.getTitle());
        assertEquals("old/path", dto.getCoverImage());
    }

    @Test
    void getPlayListsByUser_shouldReturnUserPlayLists() {
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlayListEntity> page = new PageImpl<>(List.of(playList));
        when(playListRepository.getPlayListsByUserId(user.getId(), pageable)).thenReturn(page);
        when(playListMapper.toDto(any())).thenReturn(playListDto);

        Page<PlayListDto> result = playListService.getPlayListsByUser("user@example.com", pageable);

        List<PlayListDto> content = result.getContent();
        assertNotNull(content);
        assertEquals(1, content.size());
        PlayListDto dto = content.get(0);
        assertEquals("My Playlist", dto.getTitle());
        assertEquals("old/path", dto.getCoverImage());
    }

    @Test
    void addSongToPlayList_shouldAddSong_whenNotAlreadyPresent_andOwnerValid() {
        when(songRepository.findById(100L)).thenReturn(Optional.of(song));
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        PlayListEntity updated = PlayListEntity.builder()
                .id(1)
                .title("My Playlist")
                .user(user)
                .coverImage("old/path")
                .songs(Set.of(song))
                .build();
        when(playListRepository.save(any())).thenReturn(updated);
        when(playListMapper.toDto(any())).thenReturn(playListDto);

        PlayListDto result = playListService.addSongToPlayList(100L, 1, "user@example.com");

        assertNotNull(result);
        assertEquals("My Playlist", result.getTitle());
    }

    @Test
    void addSongToPlayList_shouldThrowSongExists_whenSongAlreadyInPlayList() {
        when(songRepository.findById(100L)).thenReturn(Optional.of(song));
        playList.getSongs().add(song);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.addSongToPlayList(100L, 1, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.SONG_EXISTS, ex.getErrorCode());
    }

    @Test
    void addSongToPlayList_shouldThrowPlaylistMismatch_whenOwnershipInvalid() {
        when(songRepository.findById(100L)).thenReturn(Optional.of(song));
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("other@example.com")).thenReturn(anotherUser);

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.addSongToPlayList(100L, 1, "other@example.com")
        );
        assertEquals(BusinessErrorCodes.PLAYLIST_MISMATCH, ex.getErrorCode());
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
        when(songRepository.findById(100L)).thenReturn(Optional.of(song));
        playList.getSongs().add(song);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        playListService.removeSongFromPlayList(100L, 1, "user@example.com");
        assertFalse(playList.getSongs().contains(song));
        verify(playListRepository, times(1)).save(playList);
    }

    @Test
    void removeSongFromPlayList_shouldThrowSongNotExists_whenSongNotInPlayList() {
        when(songRepository.findById(100L)).thenReturn(Optional.of(song));
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.removeSongFromPlayList(100L, 1, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.SONG_NOT_EXISTS, ex.getErrorCode());
    }

    @Test
    void removeSongFromPlayList_shouldThrowPlaylistMismatch_whenOwnershipInvalid() {
        when(songRepository.findById(100L)).thenReturn(Optional.of(song));
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("other@example.com")).thenReturn(anotherUser);

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.removeSongFromPlayList(100L, 1, "other@example.com")
        );
        assertEquals(BusinessErrorCodes.PLAYLIST_MISMATCH, ex.getErrorCode());
    }

    @Test
    void getPlayListTracks_shouldReturnSongDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SongEntity> page = new PageImpl<>(List.of(song));
        when(songRepository.findByPlaylistId(1, pageable)).thenReturn(page);

        SongDto expectedSongDto = new SongDto();
        expectedSongDto.setTitle("Song 1");
        when(songMapper.toDto(any(SongEntity.class))).thenReturn(expectedSongDto);

        Page<SongDto> result = playListService.getPlayListTracks(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Song 1", result.getContent().get(0).getTitle());
    }

    @Test
    void updatePlayList_shouldUpdateTitleAndImage_whenProvided() {
        Integer playListId = 1;
        String title = "New Title";

        when(playListImage.isEmpty()).thenReturn(false);

        PlayListEntity updatedPlayList = PlayListEntity.builder()
                .id(1)
                .title("New Title")
                .coverImage("new/image/path")
                .user(user)
                .build();

        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(fileService.updateFile(any(), eq("old/path"), any())).thenReturn("new/image/path");
        when(playListRepository.save(any())).thenReturn(updatedPlayList);

        PlayListDto expectedDto = new PlayListDto();
        expectedDto.setTitle("New Title");
        expectedDto.setCoverImage("new/image/path");
        when(playListMapper.toDto(any())).thenReturn(expectedDto);

        PlayListDto result = playListService.updatePlayList(playListId, title, "user@example.com", playListImage);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("new/image/path", result.getCoverImage());
    }

    @Test
    void updatePlayList_shouldUpdateOnlyTitle_whenImageNullOrEmpty() {
        Integer playListId = 1;
        String title = "New Title";

        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(playListRepository.save(playList)).thenReturn(playList);

        PlayListDto expectedDto = new PlayListDto();
        expectedDto.setTitle("New Title");
        expectedDto.setCoverImage("old/path");
        when(playListMapper.toDto(any())).thenReturn(expectedDto);

        PlayListDto result = playListService.updatePlayList(playListId, title, "user@example.com", null);

        assertNotNull(result, "DTO nie może być null");
        assertEquals("New Title", result.getTitle());
        assertEquals("old/path", result.getCoverImage());
    }

    @Test
    void updatePlayList_shouldNotUpdateTitle_whenTitleIsEmpty() {
        Integer playListId = 1;
        String title = "";

        when(playListRepository.findById(playListId)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(playListRepository.save(playList)).thenReturn(playList);
        when(playListMapper.toDto(any())).thenReturn(playListDto);

        PlayListDto result = playListService.updatePlayList(playListId, title, "user@example.com", null);

        assertNotNull(result);
        assertEquals("My Playlist", result.getTitle());
        assertEquals("old/path", result.getCoverImage());
        verify(playListRepository).save(playList);
    }

    @Test
    void updatePlayList_shouldNotUpdateImage_whenImageIsEmpty() {
        Integer playListId = 1;
        String title = "New Title";

        when(playListImage.isEmpty()).thenReturn(true);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(playListRepository.save(playList)).thenReturn(playList);
        when(playListMapper.toDto(any())).thenReturn(playListDto);

        PlayListDto result = playListService.updatePlayList(playListId, title, "user@example.com", playListImage);

        verify(fileService, never()).updateFile(any(), anyString(), anyString());
        assertEquals("old/path", result.getCoverImage());
    }

    @Test
    void deletePlayList_shouldDeletePlayList_whenNotFavourite_andOwnershipValid() {
        playList.setFavourite(false);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        playListService.deletePlayList(1, "user@example.com");
        verify(playListRepository, times(1)).delete(playList);
        verify(fileService, times(1)).deleteFile(playList.getCoverImage());
    }

    @Test
    void deletePlayList_shouldProcessFavourite_whenFavouriteTrue() {
        playList.setFavourite(true);
        playList.getSongs().add(song);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        playListService.deletePlayList(1, "user@example.com");
        verify(songLikeRepository, times(1)).deleteBySong(song);
        verify(playListRepository, times(1)).delete(playList);
        verify(fileService, times(1)).deleteFile(playList.getCoverImage());
    }

    @Test
    void deletePlayList_shouldThrowPlaylistMismatch_whenOwnershipInvalid() {
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(userService.findUserByEmail("other@example.com")).thenReturn(anotherUser);

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.deletePlayList(1, "other@example.com")
        );
        assertEquals(BusinessErrorCodes.PLAYLIST_MISMATCH, ex.getErrorCode());
        verify(playListRepository, never()).delete(any());
        verify(fileService, never()).deleteFile(anyString());
    }

    @Test
    void addSongToPlayList_shouldThrowSongNotFound_whenSongDoesNotExist() {
        when(songRepository.findById(100L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.addSongToPlayList(100L, 1, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.SONG_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void removeSongFromPlayList_shouldThrowSongNotFound_whenSongDoesNotExist() {
        when(songRepository.findById(100L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                playListService.removeSongFromPlayList(100L, 1, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.SONG_NOT_FOUND, ex.getErrorCode());
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

        when(playListRepository.save(any(PlayListEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        playListService.addToFavourites(user, song);

        ArgumentCaptor<PlayListEntity> captor = ArgumentCaptor.forClass(PlayListEntity.class);
        verify(playListRepository, times(2)).save(captor.capture());

        List<PlayListEntity> savedPlayLists = captor.getAllValues();
        PlayListEntity updatedPlaylist = savedPlayLists.get(1);

        assertEquals(1, updatedPlaylist.getSongs().size());
        assertTrue(updatedPlaylist.getSongs().contains(song));
    }

}
