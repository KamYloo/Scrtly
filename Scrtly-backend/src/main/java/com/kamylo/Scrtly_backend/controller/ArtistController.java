package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.response.PagedResponse;
import com.kamylo.Scrtly_backend.service.ArtistService;
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
@RequestMapping("/artists")
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDto> getArtist(@PathVariable Long artistId, Principal principal) {
        ArtistDto artist = artistService.getArtistProfile(artistId, principal.getName());
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
