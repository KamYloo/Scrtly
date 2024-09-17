package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.AlbumRequest;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.AlbumService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.midi.Track;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    @Autowired
    private AlbumService albumService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Album> createAlbumHandler(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("title") String title,
                                                    @RequestHeader("Authorization") String token) throws ArtistException, UserException {
        if (file.isEmpty()) {
            throw new RuntimeException("Image file not uploaded.");
        }
        User user = userService.findUserProfileByJwt(token);
        Artist artist = (Artist) user;

        try {

            Path folderPath = Paths.get("src/main/resources/static/uploads/albumImages");

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            AlbumRequest albumRequest = new AlbumRequest();
            albumRequest.setTitle(title);
            albumRequest.setArtist(artist);
            albumRequest.setCoverImage("/uploads/albumImages/" + fileName);
            Album album = albumService.createAlbum(albumRequest);
            return new ResponseEntity<>(album, HttpStatus.CREATED);
        }
        catch (IOException e) {
            throw new RuntimeException("Error saving image file.", e);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Album>> getAllAlbumsHandler() {
        List<Album> albums = albumService.getAllAlbums();
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<List<Song>> getAlbumTracksHandler(@PathVariable Integer albumId) throws AlbumException {
        List<Song> songs = albumService.getAlbumTracks(albumId);
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{albumId}")
    public ResponseEntity<ApiResponse> deleteAlbumHandler(@PathVariable Integer albumId , @RequestHeader("Authorization") String token) throws UserException, AlbumException, ArtistException {
        User user = userService.findUserProfileByJwt(token);
        Artist artist = (Artist) user;
        ApiResponse res = new ApiResponse();

        try {
            Album album = albumService.getAlbum(albumId);
            if (album == null) {
                throw new AlbumException("Album not found with id " + albumId);
            }

            albumService.deleteAlbum(albumId, artist.getId());
            res.setMessage("Album deleted successfully.");
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (ArtistException | AlbumException e) {
            res.setMessage(e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        }
    }
}
