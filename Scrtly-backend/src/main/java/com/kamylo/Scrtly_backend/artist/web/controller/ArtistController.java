package com.kamylo.Scrtly_backend.artist.web.controller;

import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.artist.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/artist")
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDto> getArtist(@PathVariable Long artistId, Principal principal) {
        String username = (principal != null ? principal.getName() : null);
        ArtistDto artist = artistService.getArtistProfile(artistId, username);
        return new ResponseEntity<>(artist, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<ArtistDto>> getArtists(@PageableDefault(size = 9) Pageable pageable) {
       Page<ArtistDto> artists = artistService.getArtists(pageable);
       return new ResponseEntity<>(PagedResponse.of(artists), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ArtistDto> updateArtistHandler(@RequestPart("bannerImg") MultipartFile bannerImg,
                                                         @RequestPart("artistBio") String artistBio,
                                                         Principal principal) {

        ArtistDto updatedArtist = artistService.updateArtist(principal.getName(),bannerImg,artistBio);
        return new ResponseEntity<>(updatedArtist, HttpStatus.OK);
    }

    @GetMapping("/{artistId}/tracks")
    public ResponseEntity<PagedResponse<SongDto>> getArtistTracksHandler(@PathVariable Long artistId, @PageableDefault(size = 9) Pageable pageable) {
        Page<SongDto> songs = artistService.getArtistTracks(artistId, pageable);
        return new ResponseEntity<>(PagedResponse.of(songs), HttpStatus.OK);
    }
}
