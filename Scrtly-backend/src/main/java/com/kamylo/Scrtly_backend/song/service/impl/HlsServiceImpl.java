package com.kamylo.Scrtly_backend.song.service.impl;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HlsServiceImpl implements HlsService {

    @Value("${application.hls.directory}")
    private String hlsBasePath;

    @Value("${application.file.image-dir}")
    private String storageBasePath;

    private static final LinkedHashMap<String, Integer> AUDIO_RATES;
    static {
        AUDIO_RATES = new LinkedHashMap<>();
        AUDIO_RATES.put("320k", 320_000);
        AUDIO_RATES.put("256k", 256_000);
        AUDIO_RATES.put("128k", 128_000);
        AUDIO_RATES.put("64k",  64_000);
    }

    @Override
    public String generateHls(String inputFilePath, Long songId) {
        try {
            return generateHlsInternal(inputFilePath, songId);
        } catch (Exception e) {
            throw new CustomException(BusinessErrorCodes.HLS_GENERATION_FAILED, e);
        }
    }

    public String generateHlsInternal(String inputFilePath, Long songId) throws IOException, InterruptedException {
        Path baseDir = Paths.get(hlsBasePath, songId.toString());
        if (!Files.exists(Paths.get(inputFilePath))) {
            throw new CustomException(BusinessErrorCodes.HLS_GENERATION_FAILED);
        }
        Files.createDirectories(baseDir);

        for (Map.Entry<String, Integer> entry : AUDIO_RATES.entrySet()) {
            String rate = entry.getKey();
            Path rateDir = baseDir.resolve(rate);
            Files.createDirectories(rateDir);

            List<String> cmd = List.of(
                    "ffmpeg", "-i", inputFilePath,
                    "-vn", "-c:a", "aac", "-b:a", rate,
                    "-f", "hls", "-hls_time", "10",
                    "-hls_list_size", "0",
                    "-hls_segment_filename", rateDir.resolve("seg_%03d.ts").toString(),
                    rateDir.resolve("prog.m3u8").toString()
            );

            Process p = new ProcessBuilder(cmd).inheritIO().start();
            if (p.waitFor() != 0) {
                throw new CustomException(BusinessErrorCodes.HLS_GENERATION_FAILED);
            }
        }

        // write manifests
        String master = buildMasterManifest(AUDIO_RATES);
        Files.writeString(baseDir.resolve("master.m3u8"), master);

        String premium = buildMasterManifest(AUDIO_RATES);
        Files.writeString(baseDir.resolve("premium.m3u8"), premium);

        return "/song/" + songId + "/hls/master";
    }

    private String buildMasterManifest(Map<String,Integer> rates) {
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n#EXT-X-VERSION:3\n");
        rates.forEach((rate, bw) -> {
            sb.append(String.format("#EXT-X-STREAM-INF:BANDWIDTH=%d,NAME=\"%s\"\n", bw, rate));
            sb.append(rate).append("/prog.m3u8\n");
        });
        return sb.toString();
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
}

