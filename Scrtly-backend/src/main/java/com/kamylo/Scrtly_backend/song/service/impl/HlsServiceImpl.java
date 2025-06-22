package com.kamylo.Scrtly_backend.song.service.impl;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class HlsServiceImpl implements HlsService {

    @Value("${application.hls.directory}")
    private String hlsBasePath;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Value("${application.file.image-dir}")
    private String storageBasePath;

    private static final Map<String, Integer> AUDIO_RATES = Map.of(
            "64k", 64_000,
            "128k", 128_000,
            "256k", 256_000
    );

    @Async("hlsExecutor")
    @Override
    public CompletableFuture<String> generateHls(String inputFilePath, Long songId) {
        Path baseDir = Paths.get(hlsBasePath, songId.toString());
        try {
            if (!Files.exists(Paths.get(inputFilePath))) {
                throw new CustomException(BusinessErrorCodes.HLS_GENERATION_FAILED);
            }
            Files.createDirectories(baseDir);

            for (String rate : AUDIO_RATES.keySet()) {
                Path rateDir = baseDir.resolve(rate);
                Files.createDirectories(rateDir);

                List<String> cmd = List.of(
                        "ffmpeg",
                        "-i", inputFilePath,
                        "-vn",
                        "-c:a", "aac",
                        "-b:a", rate,
                        "-f", "hls",
                        "-hls_time", "10",
                        "-hls_list_size", "0",
                        "-hls_segment_filename", rateDir.resolve("seg_%03d.ts").toString(),
                        rateDir.resolve("prog.m3u8").toString()
                );

                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.inheritIO();
                Process p = pb.start();
                int exit = p.waitFor();
                if (exit != 0) {
                    throw new CustomException(BusinessErrorCodes.HLS_GENERATION_FAILED);
                }
            }

            Path master = baseDir.resolve("master.m3u8");
            String masterContent = createMasterManifest();
            Files.writeString(master, masterContent);

            String manifestUrl = cdnBaseUrl + "hls/" + songId + "/master.m3u8";
            return CompletableFuture.completedFuture(manifestUrl);

        } catch (IOException | InterruptedException e) {
            throw new CustomException(BusinessErrorCodes.HLS_GENERATION_FAILED, e);
        }
    }

    @Override
    public void deleteHlsFolder(Long songId) {
        Path hlsDir = Paths.get(storageBasePath, "hls", songId.toString());
        try {
            FileSystemUtils.deleteRecursively(hlsDir);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to delete HLS folder for song " + songId, ex);
        }
    }

    private String createMasterManifest() {
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        for (Map.Entry<String, Integer> entry : AUDIO_RATES.entrySet()) {
            String rate = entry.getKey();
            int bw = entry.getValue();
            sb.append(String.format("#EXT-X-STREAM-INF:BANDWIDTH=%d,NAME=\"%s\"\n", bw, rate));
            sb.append(rate).append("/prog.m3u8\n");
        }
        return sb.toString();
    }
}

