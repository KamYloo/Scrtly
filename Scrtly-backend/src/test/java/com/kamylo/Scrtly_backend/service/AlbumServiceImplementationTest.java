package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.request.AlbumRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlbumServiceImplementationTest {

    @InjectMocks
    private AlbumServiceImplementation albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserService userService;

    @Mock
    private ArtistService artistService;

    @Mock
    private FileServiceImplementation fileService;

    @Mock
    private MultipartFile albumImage;

    private Album album;
    private Artist artist;
    private AlbumRequest albumRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        artist = new Artist();
        artist.setId(1L);

        album = new Album();
        album.setId(1);
        album.setTitle("Test Album");
        album.setArtist(artist);
        album.setReleaseDate(LocalDate.now());

        albumRequest = new AlbumRequest();
        albumRequest.setTitle("Test Album");
        albumRequest.setArtist(artist);
    }

    @Test
    void createAlbumSuccess() throws UserException, ArtistException {
        when(userService.findUserById(artist.getId())).thenReturn(artist);
        when(albumImage.isEmpty()).thenReturn(false);

        String mockFileName = "image.jpg";
        when(fileService.saveFile(albumImage, "/uploads/albumImages")).thenReturn(mockFileName);

        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> {
            Album album = invocation.getArgument(0);
            album.setId(1);
            return album;
        });

        Album createdAlbum = albumService.createAlbum(albumRequest, albumImage);
        assertNotNull(createdAlbum);
        assertEquals("Test Album", createdAlbum.getTitle());

        String expectedImagePath = "/uploads/albumImages/" + mockFileName;
        assertEquals(expectedImagePath, createdAlbum.getCoverImage());

        verify(fileService, times(1)).saveFile(albumImage, "/uploads/albumImages");
        verify(albumRepository, times(1)).save(any(Album.class));
    }

    @Test
    void createAlbumThrowsArtistExceptionForNonArtistUser() throws UserException {
        User nonArtistUser = new User();
        nonArtistUser.setId(2L);

        when(userService.findUserById(artist.getId())).thenReturn(nonArtistUser);

        assertThrows(ArtistException.class, () -> albumService.createAlbum(albumRequest, albumImage));
    }

    @Test
    void getAllAlbumsReturnsAlbumsList() {
        List<Album> albumList = List.of(album);
        when(albumRepository.findAllByOrderByIdDesc()).thenReturn(albumList);

        List<Album> result = albumService.getAllAlbums();
        assertEquals(1, result.size());
        assertEquals(album, result.get(0));
    }

    @Test
    void getAlbumsByArtistReturnsAlbumsList() throws ArtistException {
        when(artistService.getArtistById(artist.getId())).thenReturn(artist);
        artist.setAlbums(List.of(album));

        List<Album> albums = albumService.getAlbumsByArtist(artist.getId());
        assertEquals(1, albums.size());
        assertEquals(album, albums.get(0));
    }

    @Test
    void getAlbumsByArtistThrowsArtistExceptionForNonExistingArtist() throws ArtistException {
        when(artistService.getArtistById(artist.getId())).thenThrow(new ArtistException("Artist not found"));

        assertThrows(ArtistException.class, () -> albumService.getAlbumsByArtist(artist.getId()));
    }

    @Test
    void getAlbumTracksReturnsTrackList() throws AlbumException {
        List<Song> songs = List.of(new Song(), new Song());
        album.setSongs(songs);

        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));

        List<Song> result = albumService.getAlbumTracks(album.getId());
        assertEquals(2, result.size());
    }

    @Test
    void getAlbumThrowsAlbumExceptionForNonExistingAlbum() {
        when(albumRepository.findById(album.getId())).thenReturn(Optional.empty());

        assertThrows(AlbumException.class, () -> albumService.getAlbum(album.getId()));
    }

    @Test
    void deleteAlbumSuccess() throws AlbumException, ArtistException {
        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));
        album.setArtist(artist);

        albumService.deleteAlbum(album.getId(), artist.getId());

        verify(albumRepository, times(1)).deleteById(album.getId());
        verify(fileService, times(1)).deleteFile(album.getCoverImage());
    }

    @Test
    void deleteAlbumThrowsAlbumExceptionForNonExistingAlbum() {
        when(albumRepository.findById(album.getId())).thenReturn(Optional.empty());

        assertThrows(AlbumException.class, () -> albumService.deleteAlbum(album.getId(), artist.getId()));
    }

    @Test
    void deleteAlbumThrowsArtistExceptionForMismatchedArtist() {
        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));
        Artist otherArtist = new Artist();
        otherArtist.setId(2L);

        album.setArtist(otherArtist);

        assertThrows(ArtistException.class, () -> albumService.deleteAlbum(album.getId(), artist.getId()));
    }
}

