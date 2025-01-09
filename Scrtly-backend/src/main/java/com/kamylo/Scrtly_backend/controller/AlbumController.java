package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.response.PagedResponse;
import com.kamylo.Scrtly_backend.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/create")
    public ResponseEntity<AlbumDto> createAlbum(@RequestParam(value = "file", required = false) MultipartFile file,
                                                       @RequestParam("title") String title,
                                                       Principal principal) {

        AlbumDto albumDto = albumService.createAlbum(title, file, principal.getName());
        return new ResponseEntity<>(albumDto, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<AlbumDto>> getAlbums(@PageableDefault(size = 9) Pageable pageable) {
        Page<AlbumDto> albums = albumService.getAlbums(pageable);
        return new ResponseEntity<>(PagedResponse.of(albums), HttpStatus.OK);
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<AlbumDto>> getAlbumsByArtist(@PathVariable("artistId") Long artistId) {
        List<AlbumDto> albums = albumService.getAlbumsByArtist(artistId);
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable Integer albumId) {
        AlbumDto album = albumService.getAlbum(albumId);
        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<List<SongDto>> getAlbumTracks(@PathVariable Integer albumId) {
        List<SongDto> songs = albumService.getAlbumTracks(albumId);
        return new  ResponseEntity<>(songs, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{albumId}")
    public ResponseEntity<?> deleteAlbum(@PathVariable Integer albumId , Principal principal) {
        albumService.deleteAlbum(albumId, principal.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
