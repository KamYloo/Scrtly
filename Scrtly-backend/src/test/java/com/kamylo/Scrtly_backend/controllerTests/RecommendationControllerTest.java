package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.metrics.service.MetricsService;
import com.kamylo.Scrtly_backend.metrics.web.controller.RecommendationController;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private RecommendationController controller;

    private ArtistDto artist(Long id, String name) {
        ArtistDto a = new ArtistDto();
        a.setId(id);
        a.setPseudonym(name);
        return a;
    }

    private SongDto song(Long id, String title) {
        SongDto s = new SongDto();
        s.setId(id);
        s.setTitle(title);
        return s;
    }

    private AlbumDto album() {
        AlbumDto a = new AlbumDto();
        a.setId(Math.toIntExact(100L));
        a.setTitle("al");
        return a;
    }

    @Test
    void topArtists_callsService_withGivenWindowAndN_andReturnsOk() {
        List<ArtistDto> expected = List.of(artist(1L, "A"), artist(2L, "B"));
        when(metricsService.getTopArtists("7d", 5)).thenReturn(expected);

        ResponseEntity<List<ArtistDto>> resp = controller.topArtists("7d", 5);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(expected, resp.getBody());
        verify(metricsService, times(1)).getTopArtists("7d", 5);
    }

    @Test
    void topSongs_callsService_withGivenWindowAndN_andReturnsOk() {
        List<SongDto> expected = List.of(song(10L, "s1"), song(11L, "s2"));
        when(metricsService.getTopSongs("month", 3)).thenReturn(expected);

        ResponseEntity<List<SongDto>> resp = controller.topSongs("month", 3);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(expected, resp.getBody());
        verify(metricsService, times(1)).getTopSongs("month", 3);
    }

    @Test
    void topAlbums_callsService_withGivenWindowAndN_andReturnsOk() {
        List<AlbumDto> expected = List.of(album());
        when(metricsService.getTopAlbums("all", 10)).thenReturn(expected);

        ResponseEntity<List<AlbumDto>> resp = controller.topAlbums("all", 10);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(expected, resp.getBody());
        verify(metricsService, times(1)).getTopAlbums("all", 10);
    }
}