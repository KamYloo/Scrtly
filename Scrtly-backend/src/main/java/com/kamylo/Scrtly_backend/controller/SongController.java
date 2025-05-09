package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.dto.request.SongRequest;
import com.kamylo.Scrtly_backend.service.SongService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.security.Principal;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/song")
public class SongController {

    private final SongService songService;

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

    @DeleteMapping("/delete/{songId}")
    public ResponseEntity<?> deleteSongHandler(@PathVariable Long songId, Principal principal) {
       songService.deleteSong(songId, principal.getName());
        return ResponseEntity.ok(songId);
    }

}
