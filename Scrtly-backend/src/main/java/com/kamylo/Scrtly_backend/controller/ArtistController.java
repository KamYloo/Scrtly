package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.mapper.ArtistDtoMapper;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.service.ArtistService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.util.ArtistUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        ArtistDto artistDto = ArtistDtoMapper.toArtistDto(artist);
        artistDto.setReq_artist(ArtistUtil.isReqArtist(reqUser, artist));
        return new ResponseEntity<>(artistDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/")
    public ResponseEntity<List<ArtistDto>> getAllArtists() {
       List<Artist> artists = artistService.getAllArtists();
       List<ArtistDto> artistDtos = ArtistDtoMapper.toArtistDtos(artists);
       return new ResponseEntity<>(artistDtos, HttpStatus.OK);
    }
}
