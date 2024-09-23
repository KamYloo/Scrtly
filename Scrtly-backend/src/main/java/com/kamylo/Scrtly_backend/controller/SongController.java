package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.dto.mapper.SongDtoMapper;
import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.service.AlbumService;
import com.kamylo.Scrtly_backend.service.SongService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    @Autowired
    private SongService songService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<SongDto> createSongHandler(@RequestParam("title") String title,
                                                     @RequestParam("albumId") Integer albumId,
                                                     @RequestParam("imageSong") MultipartFile imageFile,
                                                     @RequestParam("audioFile") MultipartFile audioFile,
                                                     @RequestHeader("Authorization") String token) throws UserException, AlbumException, UnsupportedAudioFileException, IOException {
        if (imageFile.isEmpty() || audioFile.isEmpty()) {
            throw new RuntimeException("Image file not uploaded.");
        }
        User user = (User) userService.findUserProfileByJwt(token);
        Album album = albumService.getAlbum(albumId);
        Song song = songService.createSong(title, album, (Artist) user , imageFile, audioFile);
        SongDto songDto = SongDtoMapper.toSongDto(song, user);
        return new ResponseEntity<>(songDto, HttpStatus.CREATED);
    }
}
