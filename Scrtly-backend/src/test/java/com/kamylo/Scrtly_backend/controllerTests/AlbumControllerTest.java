package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.album.web.controller.AlbumController;
import com.kamylo.Scrtly_backend.album.web.dto.request.AlbumCreateRequest;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.metrics.messaging.publisher.MetricsPublisher;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.album.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    @Mock
    private AlbumService albumService;

    @Mock
    private MetricsPublisher metricsPublisher;

    @InjectMocks
    private AlbumController controller;

    @Mock
    private Principal principal;

    private AlbumDto sampleAlbumDto(Integer id, String title) {
        AlbumDto dto = new AlbumDto();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }

    private SongDto sampleSongDto() {
        SongDto s = new SongDto();
        s.setDuration(180);
        return s;
    }

    @Test
    void createAlbum_returnsCreated_andCallsService() {
        String title = "New Album";
        MultipartFile file = mock(MultipartFile.class);

        AlbumCreateRequest req = new AlbumCreateRequest();
        req.setTitle(title);
        req.setFile(file);

        when(principal.getName()).thenReturn("artist@example.com");

        AlbumDto dto = sampleAlbumDto(1, title);
        when(albumService.createAlbum(eq(title), eq(file), eq("artist@example.com"))).thenReturn(dto);

        var response = controller.createAlbum(req, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(albumService, times(1)).createAlbum(title, file, "artist@example.com");
    }

    @Test
    void getAlbums_callsService_withCorrectPageable_andReturnsOk() {
        AlbumDto dto = sampleAlbumDto(2, "Paged");
        when(albumService.getAlbums(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));

        var response = controller.getAlbums(0, 9);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(albumService).getAlbums(captor.capture());
        Pageable p = captor.getValue();
        assertEquals(0, p.getPageNumber());
        assertEquals(9, p.getPageSize());
    }

    @Test
    void getAlbumsByArtist_callsService_withQueryAndReturnsOk() {
        Long artistId = 42L;
        String query = "search";
        AlbumDto dto = sampleAlbumDto(3, "ArtistAlbum");
        when(albumService.getAlbumsByArtist(eq(artistId), eq(query), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        var response = controller.getAlbumsByArtist(artistId, query, 1, 5);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(albumService).getAlbumsByArtist(eq(artistId), eq(query), captor.capture());
        Pageable p = captor.getValue();
        assertEquals(1, p.getPageNumber());
        assertEquals(5, p.getPageSize());
    }

    @Test
    void getAlbum_publishesMetric_andReturnsAlbum() {
        Integer albumId = 7;
        AlbumDto dto = sampleAlbumDto(albumId, "Single");
        when(albumService.getAlbum(albumId)).thenReturn(dto);

        var response = controller.getAlbum(albumId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(metricsPublisher, times(1)).publishAlbumView(albumId);
        verify(albumService, times(1)).getAlbum(albumId);
    }

    @Test
    void getAlbumTracks_returnsListOfSongs() {
        Integer albumId = 11;
        SongDto s1 = sampleSongDto();
        when(albumService.getAlbumTracks(albumId)).thenReturn(List.of(s1));

        var response = controller.getAlbumTracks(albumId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<SongDto> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals(180, body.get(0).getDuration());
        verify(albumService).getAlbumTracks(albumId);
    }

    @Test
    void deleteAlbum_callsService_andReturnsOkWithId() {
        Integer albumId = 99;
        when(principal.getName()).thenReturn("user@example.com");

        var response = controller.deleteAlbum(albumId, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(albumId, response.getBody());
        verify(albumService, times(1)).deleteAlbum(albumId, "user@example.com");
    }
}