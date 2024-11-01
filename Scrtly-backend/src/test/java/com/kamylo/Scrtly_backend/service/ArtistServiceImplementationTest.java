package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ArtistServiceImplementationTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistServiceImplementation artistService;

    private Artist artist;

    @BeforeEach
    void setUp() {
        artist = new Artist();
        artist.setId(1L);
        artist.setArtistName("Test Artist");
        artist.setArtistBio("Sample bio");
    }

    @Test
    void getAllArtistsSuccess() {
        when(artistRepository.findAll()).thenReturn(List.of(artist));

        List<Artist> result = artistService.getAllArtists();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Artist", result.get(0).getArtistName());
        verify(artistRepository, times(1)).findAll();
    }

    @Test
    void getArtistByIdSuccess() throws ArtistException {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

        Artist foundArtist = artistService.getArtistById(1L);

        assertNotNull(foundArtist);
        assertEquals("Test Artist", foundArtist.getArtistName());
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void getArtistByIdThrowsException() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());

        ArtistException exception = assertThrows(ArtistException.class, () -> {
            artistService.getArtistById(1L);
        });

        assertEquals("Artist not found with id 1", exception.getMessage());
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void searchArtistsByNameSuccess() throws ArtistException {
        when(artistRepository.findByArtistName("Test Artist")).thenReturn(Set.of(artist));

        Set<Artist> result = artistService.searchArtistsByName("Test Artist");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(artist));
        verify(artistRepository, times(1)).findByArtistName("Test Artist");
    }

    @Test
    void updateArtistSuccess() throws ArtistException {
        String newBannerImg = "/new/banner/image.jpg";
        String newBio = "Updated bio";

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Artist updatedArtist = artistService.updateArtist(1L, newBannerImg, newBio);

        assertNotNull(updatedArtist);
        assertEquals(newBannerImg, updatedArtist.getBannerImg());
        assertEquals(newBio, updatedArtist.getArtistBio());
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    void updateArtistWithoutBanner() throws ArtistException {
        String currentBanner = "/old/banner/image.jpg";
        artist.setBannerImg(currentBanner);

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Artist updatedArtist = artistService.updateArtist(1L, "", "New bio without banner change");

        assertNotNull(updatedArtist);
        assertEquals("", updatedArtist.getBannerImg());
        assertEquals("New bio without banner change", updatedArtist.getArtistBio());
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    void getArtistTracksSuccess() throws ArtistException {
        Song song1 = new Song();
        song1.setTitle("Track 1");
        Song song2 = new Song();
        song2.setTitle("Track 2");

        artist.setSongs(List.of(song1, song2));

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

        List<Song> result = artistService.getArtistTracks(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Track 1", result.get(0).getTitle());
        assertEquals("Track 2", result.get(1).getTitle());
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void getArtistTracksEmpty() throws ArtistException {
        artist.setSongs(null);

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

        List<Song> result = artistService.getArtistTracks(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(artistRepository, times(1)).findById(1L);
    }
}
