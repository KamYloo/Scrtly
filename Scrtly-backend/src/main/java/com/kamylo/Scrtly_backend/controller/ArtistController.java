package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.dto.mapper.ArtistDtoMapper;
import com.kamylo.Scrtly_backend.dto.mapper.SongDtoMapper;
import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.service.ArtistService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.util.ArtistUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    @Autowired
    private ArtistService artistService;

    @Autowired
    private UserService userService;

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDto> getArtist(@PathVariable Long artistId, @RequestHeader("Authorization") String token) throws UserException, ArtistException {
        User reqUser = userService.findUserProfileByJwt(token);
        Artist artist = artistService.getArtistById(artistId);
        ArtistDto artistDto = ArtistDtoMapper.toArtistDto(artist, reqUser);
        return new ResponseEntity<>(artistDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/")
    public ResponseEntity<List<ArtistDto>> getAllArtists(@RequestHeader("Authorization") String token) throws UserException {
       User reqUser = userService.findUserProfileByJwt(token);
       List<Artist> artists = artistService.getAllArtists();
       List<ArtistDto> artistDtos = ArtistDtoMapper.toArtistDtos(artists, reqUser);
       return new ResponseEntity<>(artistDtos, HttpStatus.OK);
    }


    @PutMapping("/update")
    public ResponseEntity<ArtistDto> updateArtistHandler(@RequestPart("bannerImg") MultipartFile bannerImg,
                                                         @RequestPart("artistBio") String artistBio,
                                                         @RequestHeader("Authorization") String token) throws ArtistException, UserException, IOException {
        Artist artist = (Artist) userService.findUserProfileByJwt(token);

        try {
            Path folderPath = Paths.get("src/main/resources/static/uploads/artistsBannerImages");

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + bannerImg.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(bannerImg.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = "uploads/artistsBannerImages/" + fileName;
            artistService.updateArtist(artist.getId(), relativePath, artistBio);
            ArtistDto artistDto = ArtistDtoMapper.toArtistDto(artist, artist);
            return new ResponseEntity<>(artistDto, HttpStatus.ACCEPTED);

        } catch (IOException e) {
            throw new RuntimeException("Error saving image file.", e);
        }
    }

    @GetMapping("/{artistId}/tracks")
    public ResponseEntity<List<SongDto>> getArtistTracksHandler(@PathVariable Long artistId, @RequestHeader("Authorization") String token) throws UserException, ArtistException {
        User user = userService.findUserProfileByJwt(token);
        List<Song> songs = artistService.getArtistTracks(artistId);
        List<SongDto> songDtos = SongDtoMapper.toSongDtoList(songs, user);
        return new ResponseEntity<>(songDtos, HttpStatus.OK);
    }
}
