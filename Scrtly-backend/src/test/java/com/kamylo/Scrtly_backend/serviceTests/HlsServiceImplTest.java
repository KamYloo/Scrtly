package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.song.service.impl.HlsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HlsServiceImplTest {

    private HlsServiceImpl hlsService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        hlsService = new HlsServiceImpl();
        ReflectionTestUtils.setField(hlsService, "hlsBasePath", tempDir.resolve("hls").toString());
        ReflectionTestUtils.setField(hlsService, "storageBasePath", tempDir.resolve("storage").toString());
    }

    @Test
    void generateHls_shouldThrow_onNonexistentInput() {
        assertThrows(CustomException.class,
                () -> hlsService.generateHls("does/not/exist.mp3", 1L));
    }

    @Test
    void generateHls_shouldWrapInternalException() throws Exception {
        HlsServiceImpl spy = Mockito.spy(hlsService);
        doThrow(new IOException("oops")).when(spy)
                .generateHlsInternal(anyString(), anyLong());
        assertThrows(CustomException.class,
                () -> spy.generateHls("file.mp3", 2L));
    }

    @Test
    void buildMasterManifest_shouldListAllRates() {
        Map<String, Integer> rates = Map.of("64k", 64000, "128k", 128000);
        String manifest = ReflectionTestUtils.invokeMethod(hlsService, "buildMasterManifest", rates);
        assertTrue(manifest.startsWith("#EXTM3U"));
        rates.keySet().forEach(rate -> assertTrue(manifest.contains(rate + "/prog.m3u8")));
    }

    @Test
    void deleteHlsFolder_deletesRecursively() throws IOException {
        long songId = 10L;
        Path dir = tempDir.resolve("storage/hls/" + songId);
        Files.createDirectories(dir);
        assertTrue(Files.exists(dir));
        hlsService.deleteHlsFolder(songId);
        assertFalse(Files.exists(dir));
    }

    @Test
    void deleteHlsFolder_throwsUnchecked_onFailure() throws IOException {
        long songId = 20L;
        Path dir = tempDir.resolve("storage/hls/" + songId);
        Files.createDirectories(dir);
        try (var mockFs = mockStatic(FileSystemUtils.class)) {
            mockFs.when(() -> FileSystemUtils.deleteRecursively(any(Path.class)))
                    .thenThrow(new IOException("fail"));
            assertThrows(UncheckedIOException.class,
                    () -> hlsService.deleteHlsFolder(songId));
        }
    }

    @Test
    void generateHlsInternal_createsDirectoriesAndManifests_whenFfmpegSucceeds() throws Exception {
        Path input = tempDir.resolve("in.mp3");
        Files.createFile(input);
        long songId = 5L;
        try (MockedConstruction<ProcessBuilder> mb = mockConstruction(ProcessBuilder.class,
                (builder, ctx) -> {
                    when(builder.inheritIO()).thenReturn(builder);
                    Process proc = mock(Process.class);
                    when(builder.start()).thenReturn(proc);
                    when(proc.waitFor()).thenReturn(0);
                })) {
            String result = ReflectionTestUtils.invokeMethod(
                    hlsService, "generateHlsInternal", input.toString(), songId);
            assertEquals("/song/5/hls/master", result);
            Path base = tempDir.resolve("hls/5");
            for (String rate : new String[]{"320k", "256k", "128k", "64k"}) {
                assertTrue(Files.isDirectory(base.resolve(rate)), "Rate dir missing: " + rate);
            }
            assertTrue(Files.exists(base.resolve("master.m3u8")));
            assertTrue(Files.exists(base.resolve("premium.m3u8")));
            assertEquals(4, mb.constructed().size(), "Expected 4 ffmpeg invocations");
        }
    }

    @Test
    void generateHlsInternal_throwsCustom_whenFfmpegNonZero() throws Exception {
        Path input = tempDir.resolve("in.mp3");
        Files.createFile(input);
        long songId = 6L;
        AtomicInteger counter = new AtomicInteger();
        try (MockedConstruction<ProcessBuilder> mb = mockConstruction(ProcessBuilder.class,
                (builder, ctx) -> {
                    when(builder.inheritIO()).thenReturn(builder);
                    Process proc = mock(Process.class);
                    when(builder.start()).thenReturn(proc);
                    boolean first = counter.getAndIncrement() == 0;
                    when(proc.waitFor()).thenReturn(first ? 1 : 0);
                })) {
            CustomException ex = assertThrows(CustomException.class,
                    () -> ReflectionTestUtils.invokeMethod(
                            hlsService, "generateHlsInternal", input.toString(), songId));
            assertNotNull(ex);
            assertEquals("HLS_GENERATION_FAILED", ex.getErrorCode().name());
            assertTrue(counter.get() >= 1);
        }
    }
}
