package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.metrics.messaging.publisher.MetricsPublisher;
import com.kamylo.Scrtly_backend.song.service.SongService;
import com.kamylo.Scrtly_backend.song.web.controller.SongController;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.song.web.dto.request.SongRequest;
import com.kamylo.Scrtly_backend.user.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.security.Principal;
import java.util.Comparator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongControllerTest {

    @Mock private SongService songService;
    @Mock private MetricsPublisher metricsPublisher;
    @Mock private UserService userService;
    @InjectMocks private SongController controller;
    @Mock private Principal principal;

    private Path tempHlsDir;

    @BeforeEach
    void setUp() throws Exception {
        tempHlsDir = Files.createTempDirectory("hls_test_");
        Field f = SongController.class.getDeclaredField("hlsBasePath");
        f.setAccessible(true);
        f.set(controller, tempHlsDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempHlsDir != null && Files.exists(tempHlsDir)) {
            Files.walk(tempHlsDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        }
    }

    private SongDto sampleSong(Long id, String title) {
        SongDto s = new SongDto();
        s.setId(id);
        s.setTitle(title);
        return s;
    }

    @Test
    void createSong_returnsCreated_andCallsService() throws Exception {
        SongRequest req = SongRequest.builder()
                .title("t")
                .albumId(1)
                .imageSong(mock(MultipartFile.class))
                .audioFile(mock(MultipartFile.class))
                .build();
        when(principal.getName()).thenReturn("artist");
        SongDto dto = sampleSong(1L, "t");
        when(songService.createSong(eq(req), eq("artist"))).thenReturn(dto);

        ResponseEntity<SongDto> resp = controller.createSong(req, principal);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(songService).createSong(req, "artist");
    }

    @Test
    void searchSong_returnsSet() {
        Set<SongDto> set = Set.of(sampleSong(2L, "s"));
        when(songService.searchSongByTitle("hello")).thenReturn(set);

        ResponseEntity<Set<SongDto>> resp = controller.searchSong("hello");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(set, resp.getBody());
        verify(songService).searchSongByTitle("hello");
    }

    @Test
    void recordPlay_publishesMetric() {
        controller.recordPlay(5L, 42L);
        verify(metricsPublisher).publishSongPlay(5L, 42L);
    }

    @Test
    void getMasterManifest_returnsNotFound_whenMissing() throws MalformedURLException {
        ResponseEntity<Resource> resp = controller.getMasterManifest(100L, null);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void getMasterManifest_returnsPremiumManifest_forPremiumUser() throws Exception {
        long id = 200L;
        Path dir = tempHlsDir.resolve(Long.toString(id));
        Files.createDirectories(dir);
        Path premium = dir.resolve("premium.m3u8");
        Files.writeString(premium, "#EXTM3U premium");

        when(userService.isPremium("u")).thenReturn(true);
        Principal p = mock(Principal.class);
        when(p.getName()).thenReturn("u");

        ResponseEntity<Resource> resp = controller.getMasterManifest(id, p);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(MediaType.valueOf("application/vnd.apple.mpegurl"), resp.getHeaders().getContentType());
        assertNotNull(resp.getBody());
    }

    @Test
    void getMasterManifest_returnsStandardManifest_forNonPremiumUser() throws Exception {
        long id = 201L;
        Path dir = tempHlsDir.resolve(Long.toString(id));
        Files.createDirectories(dir);
        Path master = dir.resolve("master.m3u8");
        Files.writeString(master, "#EXTM3U master");

        when(userService.isPremium("u2")).thenReturn(false);
        Principal p = mock(Principal.class);
        when(p.getName()).thenReturn("u2");

        ResponseEntity<Resource> resp = controller.getMasterManifest(id, p);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(MediaType.valueOf("application/vnd.apple.mpegurl"), resp.getHeaders().getContentType());
        assertNotNull(resp.getBody());
    }

    @Test
    void getSegment_returnsNotFound_whenMissing() throws MalformedURLException {
        ResponseEntity<Resource> resp = controller.getSegment(300L, "64k", "seg.ts");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void getSegment_returnsResource_whenExists() throws Exception {
        long id = 301L;
        Path segDir = tempHlsDir.resolve(Long.toString(id)).resolve("64k");
        Files.createDirectories(segDir);
        Path seg = segDir.resolve("segment1.ts");
        Files.writeString(seg, "data");

        ResponseEntity<Resource> resp = controller.getSegment(id, "64k", "segment1.ts");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(MediaType.valueOf("video/MP2T"), resp.getHeaders().getContentType());
        assertNotNull(resp.getBody());
    }

    @Test
    void deleteSongHandler_callsService_andReturnsOkWithId() {
        when(principal.getName()).thenReturn("owner");
        Long songId = 77L;

        ResponseEntity<?> resp = controller.deleteSongHandler(songId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(songId, resp.getBody());
        verify(songService).deleteSong(songId, "owner");
    }
}