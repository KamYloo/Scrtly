package com.kamylo.Scrtly_backend.album.web.controller;

import com.kamylo.Scrtly_backend.album.web.dto.request.AlbumCreateRequest;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.metrics.messaging.publisher.MetricsPublisher;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.album.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;
    private final MetricsPublisher metricsPublisher;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDto> createAlbum(@Valid @ModelAttribute AlbumCreateRequest request,
                                                Principal principal) {
        AlbumDto albumDto = albumService.createAlbum(request.getTitle(), request.getFile(), principal.getName());
        return new ResponseEntity<>(albumDto, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<AlbumDto>> getAlbums(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AlbumDto> albums = albumService.getAlbums(pageable);
        return new ResponseEntity<>(PagedResponse.of(albums), HttpStatus.OK);
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<PagedResponse<AlbumDto>> getAlbumsByArtist(
            @PathVariable @Positive(message = "{id.positive}") Long artistId,
            @RequestParam(value = "query", required = false) @Size(max = 200, message = "query too long") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AlbumDto> albumsPage = albumService.getAlbumsByArtist(artistId, query, pageable);
        return new ResponseEntity<>(PagedResponse.of(albumsPage), HttpStatus.OK);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable @Positive(message = "{id.positive}") Integer albumId) {
        metricsPublisher.publishAlbumView(albumId);
        AlbumDto album = albumService.getAlbum(albumId);
        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<List<SongDto>> getAlbumTracks(@PathVariable @Positive(message = "{id.positive}") Integer albumId) {
        List<SongDto> songs = albumService.getAlbumTracks(albumId);
        return new  ResponseEntity<>(songs, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{albumId}")
    public ResponseEntity<?> deleteAlbum(@PathVariable @Positive(message = "{id.positive}") Integer albumId , Principal principal) {
        albumService.deleteAlbum(albumId, principal.getName());
        return ResponseEntity.ok(albumId);
    }
}
