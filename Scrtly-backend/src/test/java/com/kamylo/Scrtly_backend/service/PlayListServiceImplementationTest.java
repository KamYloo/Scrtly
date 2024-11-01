package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.PlayListException;
import com.kamylo.Scrtly_backend.exception.SongException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.PlayListRepository;
import com.kamylo.Scrtly_backend.request.PlayListRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PlayListServiceImplementationTest {

    @Mock
    private UserService userService;

    @Mock
    private FileServiceImplementation fileService;

    @Mock
    private PlayListRepository playListRepository;

    @Mock
    private SongService songService;

    @InjectMocks
    private PlayListServiceImplementation playListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPlayList_shouldCreatePlaylist() throws UserException {
        User user = new User();
        user.setId(1L);
        PlayListRequest request = new PlayListRequest();
        request.setUser(user);
        request.setTitle("My Playlist");
        MultipartFile mockImage = mock(MultipartFile.class);
        when(userService.findUserById(1L)).thenReturn(user);
        when(playListRepository.save(any(PlayList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayList result = playListService.createPlayList(request, mockImage);

        assertNotNull(result);
        assertEquals("My Playlist", result.getTitle());
        assertEquals(user, result.getUser());
        assertEquals(LocalDate.now(), result.getCreationDate());
        verify(playListRepository, times(1)).save(any(PlayList.class));
    }

    @Test
    void getPlayList_shouldReturnPlaylist_whenExists() throws PlayListException {
        PlayList playList = new PlayList();
        playList.setId(1);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));

        PlayList result = playListService.getPlayList(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getPlayList_shouldThrowException_whenPlaylistNotFound() {
        when(playListRepository.findById(1)).thenReturn(Optional.empty());

        PlayListException exception = assertThrows(PlayListException.class, () -> playListService.getPlayList(1));
        assertEquals("PlayList not found with id: 1", exception.getMessage());
    }

    @Test
    void getPlayLists_shouldReturnAllPlaylists() {
        List<PlayList> playLists = new ArrayList<>();
        playLists.add(new PlayList());
        when(playListRepository.findAllByOrderByIdDesc()).thenReturn(playLists);

        List<PlayList> result = playListService.getPlayLists();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void addSongToPlayList_shouldAddSong_whenNotAlreadyInPlaylist() throws SongException, PlayListException {
        PlayList playList = new PlayList();
        playList.setId(1);
        Song song = new Song();
        song.setId(1L);
        when(songService.findSongById(1L)).thenReturn(song);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));
        when(playListRepository.save(any(PlayList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayList result = playListService.addSongToPlayList(1L, 1);

        assertNotNull(result);
        assertTrue(result.getSongs().contains(song));
    }

    @Test
    void addSongToPlayList_shouldThrowException_whenSongAlreadyInPlaylist() throws SongException, PlayListException {
        Song song = new Song();
        song.setId(1L);
        PlayList playList = new PlayList();
        playList.setId(1);
        playList.getSongs().add(song);
        when(songService.findSongById(1L)).thenReturn(song);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));

        SongException exception = assertThrows(SongException.class, () -> playListService.addSongToPlayList(1L, 1));
        assertEquals("Song already exists", exception.getMessage());
    }

    @Test
    void removeSongFromPlayList_shouldRemoveSong_whenSongExists() throws SongException, PlayListException {
        Song song = new Song();
        song.setId(1L);
        PlayList playList = new PlayList();
        playList.setId(1);
        playList.getSongs().add(song);
        when(songService.findSongById(1L)).thenReturn(song);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));

        playListService.removeSongFromPlayList(1L, 1);

        assertFalse(playList.getSongs().contains(song));
        verify(playListRepository, times(1)).save(playList);
    }

    @Test
    void deletePlayList_shouldDeletePlaylist_whenUserAuthorized() throws PlayListException, UserException {
        User user = new User();
        user.setId(1L);
        PlayList playList = new PlayList();
        playList.setId(1);
        playList.setUser(user);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));

        playListService.deletePlayList(1, 1L);

        verify(playListRepository, times(1)).deleteById(1);
        verify(fileService, times(1)).deleteFile(playList.getCoverImage());
    }

    @Test
    void deletePlayList_shouldThrowException_whenUserUnauthorized() throws PlayListException {
        User user = new User();
        user.setId(2L);
        PlayList playList = new PlayList();
        playList.setId(1);
        playList.setUser(user);
        when(playListRepository.findById(1)).thenReturn(Optional.of(playList));

        UserException exception = assertThrows(UserException.class, () -> playListService.deletePlayList(1, 1L));
        assertEquals("You do not have permission to delete this playlist", exception.getMessage());
    }
}
