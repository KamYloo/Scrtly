package com.kamylo.Scrtly_backend.playList.web.controller;

import com.kamylo.Scrtly_backend.playList.web.dto.PlayListDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.playList.service.PlayListService;
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
@RequestMapping("/playLists")
public class PlayListController {

    private final PlayListService playListService;

    @PostMapping("/create")
    public ResponseEntity<PlayListDto> createPlayList(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("title") String title,
                                                      Principal principal) {

        PlayListDto playList = playListService.createPlayList(title, principal.getName(), file);
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
    public ResponseEntity<PlayListDto> getPlayList(@PathVariable Integer playListId) {

        PlayListDto playList = playListService.getPlayList(playListId);
        return new ResponseEntity<>(playList, HttpStatus.OK);
    }

    @GetMapping("/{playListId}/tracks")
    public ResponseEntity<PagedResponse<SongDto>> getPlayListTracks(@PathVariable Integer playListId, @PageableDefault(size = 10) Pageable pageable) {
        Page<SongDto> songs = playListService.getPlayListTracks(playListId, pageable);
        return new ResponseEntity<>(PagedResponse.of(songs), HttpStatus.OK);
    }

    @PutMapping("/{playListId}/addSong/{songId}")
    public ResponseEntity<PlayListDto> addSongToPlayList(@PathVariable("playListId") Integer playListId,
                                                                @PathVariable("songId") Long songId,
                                                                Principal principal)  {

        PlayListDto playList = playListService.addSongToPlayList(songId, playListId, principal.getName());
        return new ResponseEntity<>(playList, HttpStatus.OK);
    }

    @DeleteMapping("/{playListId}/deleteSong/{songId}")
    public ResponseEntity<?> deleteSongFromPlayList(@PathVariable("playListId") Integer playListId,
                                                              @PathVariable("songId") Long songId,
                                                              Principal principal) {
        playListService.removeSongFromPlayList(songId, playListId, principal.getName());
        return ResponseEntity.ok(songId);
    }

    @PutMapping("/update")
    public ResponseEntity<PlayListDto> updatePlayList(Integer playListId, String title,
                                                      @RequestPart(value = "file", required = false) MultipartFile file,
                                                      Principal principal) {

        PlayListDto updatePlayList = playListService.updatePlayList(playListId, title, principal.getName(), file);
        return new ResponseEntity<>(updatePlayList, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{playListId}")
    public ResponseEntity<?> deletePlayList(@PathVariable Integer playListId, Principal principal) {
       playListService.deletePlayList(playListId, principal.getName());
       return ResponseEntity.ok(playListId);
    }
}
