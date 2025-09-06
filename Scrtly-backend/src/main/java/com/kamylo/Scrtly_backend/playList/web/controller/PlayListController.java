package com.kamylo.Scrtly_backend.playList.web.controller;

import com.kamylo.Scrtly_backend.playList.web.dto.PlayListDto;
import com.kamylo.Scrtly_backend.playList.web.dto.request.PlayListCreateRequest;
import com.kamylo.Scrtly_backend.playList.web.dto.request.PlayListUpdateRequest;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.playList.service.PlayListService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/playLists")
public class PlayListController {

    private final PlayListService playListService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlayListDto> createPlayList(@ModelAttribute @Valid PlayListCreateRequest request,
                                                      Principal principal) {

        PlayListDto playList = playListService.createPlayList(request.getTitle(), principal.getName(), request.getFile());
        return new ResponseEntity<>(playList, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<PlayListDto>> getPlayLists(@PageableDefault(size = 10) Pageable pageable) {
        Page<PlayListDto> playLists = playListService.getPlayLists(pageable);
        return new ResponseEntity<>(PagedResponse.of(playLists), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<PagedResponse<PlayListDto>> getPlayListsByReqUser(@PageableDefault(size = 10) Pageable pageable, Principal principal) {
        Page<PlayListDto> playLists = playListService.getPlayListsByUser(principal.getName(), pageable);
        return new ResponseEntity<>(PagedResponse.of(playLists), HttpStatus.OK);
    }

    @GetMapping("/{playListId}")
    public ResponseEntity<PlayListDto> getPlayList(@PathVariable @Positive(message = "{id.positive}") Integer playListId) {

        PlayListDto playList = playListService.getPlayList(playListId);
        return new ResponseEntity<>(playList, HttpStatus.OK);
    }

    @GetMapping("/{playListId}/tracks")
    public ResponseEntity<PagedResponse<SongDto>> getPlayListTracks(@PathVariable @Positive(message = "{id.positive}") Integer playListId, @PageableDefault(size = 10) Pageable pageable) {
        Page<SongDto> songs = playListService.getPlayListTracks(playListId, pageable);
        return new ResponseEntity<>(PagedResponse.of(songs), HttpStatus.OK);
    }

    @PutMapping("/{playListId}/addSong/{songId}")
    public ResponseEntity<PlayListDto> addSongToPlayList(
            @PathVariable("playListId") @Positive(message = "{id.positive}") Integer playListId,
            @PathVariable("songId") @Positive(message = "{id.positive}") Long songId,
            Principal principal)  {

        PlayListDto playList = playListService.addSongToPlayList(songId, playListId, principal.getName());
        return new ResponseEntity<>(playList, HttpStatus.OK);
    }

    @DeleteMapping("/{playListId}/deleteSong/{songId}")
    public ResponseEntity<?> deleteSongFromPlayList(
            @PathVariable("playListId") @Positive(message = "{id.positive}") Integer playListId,
            @PathVariable("songId") @Positive(message = "{id.positive}") Long songId,
            Principal principal) {
        playListService.removeSongFromPlayList(songId, playListId, principal.getName());
        return ResponseEntity.ok(songId);
    }

    @PutMapping("/update")
    public ResponseEntity<PlayListDto> updatePlayList(@ModelAttribute @Valid PlayListUpdateRequest request,
                                                      Principal principal) {

        PlayListDto updatePlayList = playListService.updatePlayList(request.getPlayListId(), request.getTitle(), principal.getName(), request.getFile());
        return new ResponseEntity<>(updatePlayList, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{playListId}")
    public ResponseEntity<?> deletePlayList(@PathVariable @Positive(message = "{id.positive}") Integer playListId, Principal principal) {
       playListService.deletePlayList(playListId, principal.getName());
       return ResponseEntity.ok(playListId);
    }
}
