package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.artist.service.ArtistService;
import com.kamylo.Scrtly_backend.artist.web.controller.ArtistController;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.artist.web.dto.request.ArtistUpdateRequest;
import com.kamylo.Scrtly_backend.metrics.messaging.publisher.MetricsPublisher;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.user.web.dto.UserMinimalDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

    @Mock private ArtistService artistService;
    @Mock private MetricsPublisher metricsPublisher;
    @InjectMocks private ArtistController controller;

    private ArtistDto sampleArtistDto;

    @BeforeEach
    void setUp() {
        sampleArtistDto = new ArtistDto();
        sampleArtistDto.setId(1L);
        sampleArtistDto.setPseudonym("artist");
    }

    private SongDto mockSongDto() {
        return mock(SongDto.class);
    }

    private UserMinimalDto mockUserMinimalDto() {
        return mock(UserMinimalDto.class);
    }

    @Test
    void getArtist_withPrincipal_shouldPublishAndReturnArtist() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");

        when(artistService.getArtistProfile(eq(1L), eq("user@example.com"))).thenReturn(sampleArtistDto);

        var response = controller.getArtist(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(sampleArtistDto, response.getBody());

        verify(metricsPublisher, times(1)).publishArtistView(1L);
        verify(artistService, times(1)).getArtistProfile(1L, "user@example.com");
    }

    @Test
    void getArtist_withoutPrincipal_shouldPassNullUsername() {
        when(artistService.getArtistProfile(eq(2L), isNull())).thenReturn(sampleArtistDto);

        var response = controller.getArtist(2L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(sampleArtistDto, response.getBody());

        verify(metricsPublisher, times(1)).publishArtistView(2L);
        verify(artistService, times(1)).getArtistProfile(eq(2L), isNull());
    }

    @Test
    void getArtists_shouldPassPaginationParametersToService() {
        List<ArtistDto> list = List.of(sampleArtistDto);
        Page<ArtistDto> page = new PageImpl<>(list);
        when(artistService.getArtists(any(Pageable.class))).thenReturn(page);

        int requestedPage = 2;
        int requestedSize = 5;
        var response = controller.getArtists(requestedPage, requestedSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(artistService, times(1)).getArtists(captor.capture());
        Pageable captured = captor.getValue();
        assertEquals(requestedPage, captured.getPageNumber());
        assertEquals(requestedSize, captured.getPageSize());
    }

    @Test
    void updateArtistHandler_shouldCallServiceAndReturnUpdatedDto() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("artistUser");

        ArtistUpdateRequest request = mock(ArtistUpdateRequest.class);
        when(request.getBannerImg()).thenReturn(null);
        when(request.getArtistBio()).thenReturn("new bio");

        ArtistDto updated = new ArtistDto();
        updated.setId(3L);
        updated.setPseudonym("updated");
        when(artistService.updateArtist(eq("artistUser"), isNull(), eq("new bio"))).thenReturn(updated);

        var response = controller.updateArtistHandler(request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());

        verify(artistService, times(1)).updateArtist("artistUser", null, "new bio");
    }

    @Test
    void getArtistTracksHandler_shouldReturnPagedTracksAndPassPaginationAndUsername() {
        SongDto song = mockSongDto();
        Page<SongDto> songsPage = new PageImpl<>(List.of(song));
        String username = "fanUser";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(artistService.getArtistTracks(eq(10L), any(Pageable.class), eq(username)))
                .thenReturn(songsPage);

        int page = 1;
        int size = 7;

        var response = controller.getArtistTracksHandler(10L, page, size, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(artistService, times(1)).getArtistTracks(eq(10L), captor.capture(), eq(username));
        Pageable captured = captor.getValue();
        assertEquals(page, captured.getPageNumber());
        assertEquals(size, captured.getPageSize());
    }

    @Test
    void getArtistTracksHandler_withoutPrincipal_shouldPassNullUsername() {
        SongDto song = mockSongDto();
        Page<SongDto> songsPage = new PageImpl<>(List.of(song));

        when(artistService.getArtistTracks(eq(11L), any(Pageable.class), isNull()))
                .thenReturn(songsPage);

        int page = 0;
        int size = 10;

        var response = controller.getArtistTracksHandler(11L, page, size, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(artistService, times(1)).getArtistTracks(eq(11L), any(Pageable.class), isNull());
    }

    @Test
    void getArtistFans_shouldPassQueryAndPaginationToService() {
        UserMinimalDto user = mockUserMinimalDto();
        Page<UserMinimalDto> fansPage = new PageImpl<>(List.of(user));
        when(artistService.getFans(eq(20L), any(Pageable.class), eq("search"))).thenReturn(fansPage);

        int page = 0;
        int size = 9;
        var response = controller.getArtistFans(20L, "search", page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(artistService, times(1)).getFans(eq(20L), captor.capture(), eq("search"));
        Pageable captured = captor.getValue();
        assertEquals(page, captured.getPageNumber());
        assertEquals(size, captured.getPageSize());
    }
}