package com.kamylo.Scrtly_backend.song.web.controller;

import com.kamylo.Scrtly_backend.metrics.service.MetricsPublisher;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongRequest;
import com.kamylo.Scrtly_backend.song.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/song")
public class SongController {
    private final SongService songService;
    private final MetricsPublisher metricsPublisher;

    @Value("${application.hls.directory}")
    private String hlsBasePath;

    @PostMapping("/upload")
    public ResponseEntity<SongDto> createSong(@RequestPart("songDetails")SongRequest songRequest,
                                              @RequestPart("imageSong") MultipartFile imageFile,
                                              @RequestPart("audioFile") MultipartFile audioFile,
                                              Principal principal) throws UnsupportedAudioFileException, IOException {

        SongDto song = songService.createSong(songRequest, principal.getName(), imageFile, audioFile);
        return new ResponseEntity<>(song, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<Set<SongDto>> searchSong(@RequestParam("title") String title) {
        Set<SongDto> songs = songService.searchSongByTitle(title);
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @PostMapping("/{id}/play")
    public void recordPlay(@PathVariable Long id) {
        metricsPublisher.publishSongPlay(id);
    }

    @GetMapping("/{id}/hls/master")
    public ResponseEntity<Resource> getMasterManifest(@PathVariable Long id) throws MalformedURLException {
        Path master = Paths.get(hlsBasePath, id.toString(), "master.m3u8");
        if (!Files.exists(master)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource resource = new UrlResource(master.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/vnd.apple.mpegurl"))
                .body(resource);
    }

    @GetMapping("/{id}/hls/{rate}/{segment}")
    public ResponseEntity<Resource> getSegment(
            @PathVariable Long id,
            @PathVariable String rate,
            @PathVariable String segment) throws MalformedURLException {
        Path seg = Paths.get(hlsBasePath, id.toString(), rate, segment);

        if (!Files.exists(seg)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new UrlResource(seg.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/MP2T"))
                .body(resource);
    }

    @DeleteMapping("/delete/{songId}")
    public ResponseEntity<?> deleteSongHandler(@PathVariable Long songId, Principal principal) {
       songService.deleteSong(songId, principal.getName());
        return ResponseEntity.ok(songId);
    }

}
