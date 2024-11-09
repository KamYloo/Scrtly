package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.PlayListDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.dto.mapper.PlayListDtoMapper;
import com.kamylo.Scrtly_backend.dto.mapper.SongDtoMapper;
import com.kamylo.Scrtly_backend.exception.PlayListException;
import com.kamylo.Scrtly_backend.exception.SongException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.PlayListRequest;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.PlayListService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/playLists")
public class PlayListController {
    @Autowired
    private PlayListService playListService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<PlayListDto> createPlayListHandler (@RequestParam("file") MultipartFile file,
                                                              @RequestParam("title") String title,
                                                              @RequestHeader("Authorization") String token) throws UserException {
        if (file.isEmpty()) {
            throw new RuntimeException("Image file not uploaded.");
        }
        User user = userService.findUserProfileByJwt(token);
        PlayListRequest playListRequest = new PlayListRequest();
        playListRequest.setTitle(title);
        playListRequest.setUser(user);
        PlayList playList = playListService.createPlayList(playListRequest, file);
        PlayListDto playListDto = PlayListDtoMapper.toPlayListDto(playList);
        return new ResponseEntity<>(playListDto, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PlayListDto>> getAllPlayListsHandler(@RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        List<PlayList> playLists = playListService.getPlayLists();
        List<PlayListDto> playListDtos = PlayListDtoMapper.toPlayListDtos(playLists);
        return new ResponseEntity<>(playListDtos, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PlayListDto>> getPlayListsByReqUserHandler(@RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        List<PlayList> playLists = playListService.getPlayListsByUser(user.getId());
        List<PlayListDto> playListDtos = PlayListDtoMapper.toPlayListDtos(playLists);
        return new ResponseEntity<>(playListDtos, HttpStatus.OK);
    }

    @GetMapping("/{playListId}")
    public ResponseEntity<PlayListDto> getPlayListHandler(@PathVariable Integer playListId, @RequestHeader("Authorization") String token) throws UserException, PlayListException {
        User user = userService.findUserProfileByJwt(token);
        PlayList playList = playListService.getPlayList(playListId);
        PlayListDto playListDto = PlayListDtoMapper.toPlayListDto(playList);
        return new ResponseEntity<>(playListDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{playListId}/tracks")
    public ResponseEntity<Set<SongDto>> getPlayListTracksHandler(@PathVariable Integer playListId, @RequestHeader("Authorization") String token) throws UserException, PlayListException {
        User user = userService.findUserProfileByJwt(token);
        Set<Song> songs = playListService.getPlayListTracks(playListId);
        Set<SongDto> songDtos = SongDtoMapper.toSongDtoListHashSet(songs,user);
        return new ResponseEntity<>(songDtos, HttpStatus.OK);
    }

    @PutMapping("/{playListId}/addSong/{songId}")
    public ResponseEntity<PlayListDto> addSongToPlayListHandler(@PathVariable("playListId") Integer playListId, @PathVariable("songId") Long songId) throws PlayListException, SongException {
        PlayList playList = playListService.addSongToPlayList(songId,playListId);
        PlayListDto playListDto = PlayListDtoMapper.toPlayListDto(playList);
        return new ResponseEntity<>(playListDto, HttpStatus.OK);
    }

    @DeleteMapping("/{playListId}/deleteSong/{songId}")
    public ResponseEntity<ApiResponse> deleteSongFromPlayListHandler(@PathVariable("playListId") Integer playListId, @PathVariable("songId") Long songId) throws PlayListException, SongException {
        ApiResponse apiResponse = new ApiResponse();
        try {
            playListService.removeSongFromPlayList(songId,playListId);
            apiResponse.setMessage("Song deleted successfully.");
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        catch (PlayListException | SongException e) {
            apiResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{playListId}")
    public ResponseEntity<PlayListDto> updatePlayList(@PathVariable Integer playListId,
                                              @RequestParam(required = false) String title,
                                              @RequestParam(required = false) MultipartFile file,
                                              @RequestHeader("Authorization") String token) throws UserException, PlayListException {
        User user = userService.findUserProfileByJwt(token);
        PlayList updatePlayList = playListService.updatePlayList(playListId, title, user.getId(), file);
        PlayListDto playListDto = PlayListDtoMapper.toPlayListDto(updatePlayList);
        return new ResponseEntity<>(playListDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{playListId}")
    public ResponseEntity<ApiResponse> deletePlayListHandler(@PathVariable Integer playListId, @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        ApiResponse res = new ApiResponse();

        try {
            playListService.deletePlayList(playListId, user.getId());
            res.setMessage("PlayList deleted successfully.");
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (UserException | PlayListException e) {
            res.setMessage(e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        }
    }
    
}
