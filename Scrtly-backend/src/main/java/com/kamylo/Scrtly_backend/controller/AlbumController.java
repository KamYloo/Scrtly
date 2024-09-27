package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.dto.mapper.AlbumDtoMapper;
import com.kamylo.Scrtly_backend.dto.mapper.SongDtoMapper;
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

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    @Autowired
    private AlbumService albumService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<AlbumDto> createAlbumHandler(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("title") String title,
                                                       @RequestHeader("Authorization") String token) throws ArtistException, UserException {
        if (file.isEmpty()) {
            throw new RuntimeException("Image file not uploaded.");
        }
        User user = userService.findUserProfileByJwt(token);
        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setTitle(title);
        albumRequest.setArtist((Artist) user);
        Album album = albumService.createAlbum(albumRequest, file);
        AlbumDto albumDto = AlbumDtoMapper.toAlbumDto(album, user);
        return new ResponseEntity<>(albumDto, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsHandler(@RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        List<Album> albums = albumService.getAllAlbums();
        List<AlbumDto> albumDtos = AlbumDtoMapper.toAlbumDtos(albums, user);
        return new ResponseEntity<>(albumDtos, HttpStatus.OK);
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<AlbumDto>> getAlbumsByArtistHandler(@PathVariable("artistId") Long artistId, @RequestHeader("Authorization") String token) throws UserException, ArtistException {
        User user = userService.findUserProfileByJwt(token);
        List<Album> albums = albumService.getAlbumsByArtist(artistId);
        List<AlbumDto> albumDtos = AlbumDtoMapper.toAlbumDtos(albums, user);
        return new ResponseEntity<>(albumDtos, HttpStatus.OK);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumHandler(@PathVariable Integer albumId, @RequestHeader("Authorization") String token) throws AlbumException, UserException {
        User user = userService.findUserProfileByJwt(token);
        Album album = albumService.getAlbum(albumId);
        AlbumDto albumDto = AlbumDtoMapper.toAlbumDto(album, user);
        return new ResponseEntity<>(albumDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<List<SongDto>> getAlbumTracksHandler(@PathVariable Integer albumId, @RequestHeader("Authorization") String token) throws AlbumException, UserException {
        User user = userService.findUserProfileByJwt(token);
        List<Song> songs = albumService.getAlbumTracks(albumId);
        List<SongDto> songDtos = SongDtoMapper.toSongDtoListArrayList(songs,user);
        return new ResponseEntity<>(songDtos, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{albumId}")
    public ResponseEntity<ApiResponse> deleteAlbumHandler(@PathVariable Integer albumId , @RequestHeader("Authorization") String token) throws UserException, AlbumException, ArtistException {
        Artist artist = (Artist) userService.findUserProfileByJwt(token);
        ApiResponse res = new ApiResponse();

        try {
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
