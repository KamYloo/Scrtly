package com.kamylo.Scrtly_backend.artist.web.controller;

import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.artist.web.dto.request.ArtistUpdateRequest;
import com.kamylo.Scrtly_backend.metrics.messaging.publisher.MetricsPublisher;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.artist.service.ArtistService;
import com.kamylo.Scrtly_backend.user.web.dto.UserMinimalDto;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/artist")
public class ArtistController {
    private final ArtistService artistService;
    private final MetricsPublisher metricsPublisher;

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDto> getArtist(@PathVariable @Positive(message = "{id.positive}") Long artistId, Principal principal) {
        metricsPublisher.publishArtistView(artistId);
        String username = (principal != null ? principal.getName() : null);
        ArtistDto artist = artistService.getArtistProfile(artistId, username);
        return new ResponseEntity<>(artist, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<ArtistDto>> getArtists(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ArtistDto> artists = artistService.getArtists(pageable);
       return new ResponseEntity<>(PagedResponse.of(artists), HttpStatus.OK);
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> updateArtistHandler(
            @Valid @ModelAttribute ArtistUpdateRequest request,
            Principal principal) {

        ArtistDto updatedArtist = artistService.updateArtist(principal.getName(), request.getBannerImg(), request.getArtistBio());
        return new ResponseEntity<>(updatedArtist, HttpStatus.OK);
    }

    @GetMapping("/{artistId}/tracks")
    public ResponseEntity<PagedResponse<SongDto>> getArtistTracksHandler(
            @PathVariable @Positive(message = "{id.positive}") Long artistId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "9") @Min(1) @Max(200) int size,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size);
        String username = (principal != null ? principal.getName() : null);
        Page<SongDto> songs = artistService.getArtistTracks(artistId, pageable, username);
        return new ResponseEntity<>(PagedResponse.of(songs), HttpStatus.OK);
    }

    @GetMapping("/{artistId}/fans")
    @Transactional(readOnly = true)
    public ResponseEntity<PagedResponse<UserMinimalDto>> getArtistFans(
            @PathVariable @Positive(message = "{id.positive}") Long artistId,
            @RequestParam(value = "query", required = false) @Size(max = 200, message = "query too long") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserMinimalDto> fansPage = artistService.getFans(artistId, pageable, query);
        return new ResponseEntity<>(PagedResponse.of(fansPage), HttpStatus.OK);
    }
}
