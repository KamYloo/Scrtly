package com.kamylo.Scrtly_backend.metrics.web.controller;

import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.metrics.service.MetricsService;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final MetricsService metricsService;

    @GetMapping("/top-artists")
    public ResponseEntity<List<ArtistDto>> topArtists(
            @RequestParam(defaultValue = "all") String window,
            @RequestParam(defaultValue = "10") int n) {
        return new ResponseEntity<>(metricsService.getTopArtists(window, n), HttpStatus.OK);
    }

    @GetMapping("/top-songs")
    public ResponseEntity<List<SongDto>> topSongs(
            @RequestParam(defaultValue = "all") String window,
            @RequestParam(defaultValue = "10") int n) {
        return new ResponseEntity<>(metricsService.getTopSongs(window, n), HttpStatus.OK);
    }

    @GetMapping("/top-albums")
    public ResponseEntity<List<AlbumDto>> topAlbums(
            @RequestParam(defaultValue = "all") String window,
            @RequestParam(defaultValue = "10") int n) {
        return ResponseEntity.ok(metricsService.getTopAlbums(window, n));
    }
}
