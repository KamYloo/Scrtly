package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.PlayListDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.entity.SongEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.dto.request.PlayListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PlayListService {
    PlayListDto createPlayList(String title, String username, MultipartFile playListImage) ;
    PlayListDto getPlayList(Integer playListId);
    Page<PlayListDto> getPlayLists(Pageable pageable);
    Page<PlayListDto> getPlayListsByUser(String username, Pageable pageable);
    PlayListDto addSongToPlayList(Long songId, Integer playListId, String username);
    void addToFavourites(UserEntity userEntity, SongEntity songEntity);
    void removeFromFavourites(UserEntity userEntity, SongEntity songEntity);
    void removeSongFromPlayList(Long songId, Integer playListId, String username);
    Page<SongDto> getPlayListTracks (Integer playListId, Pageable pageable);
    PlayListDto updatePlayList(Integer playListId, String title, String username, MultipartFile playListImage);
    void deletePlayList(Integer playListId, String username);
}
