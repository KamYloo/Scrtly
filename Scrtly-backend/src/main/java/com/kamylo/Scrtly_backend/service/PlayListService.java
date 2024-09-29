package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.PlayListException;
import com.kamylo.Scrtly_backend.exception.SongException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.request.PlayListRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface PlayListService {
    PlayList createPlayList(PlayListRequest playListRequest, MultipartFile playListImage) throws UserException;
    PlayList getPlayList(Integer playListId) throws PlayListException;
    List<PlayList> getPlayLists();
    List<PlayList> getPlayListsByUser(Long userId) throws UserException;
    PlayList addSongToPlayList(Long songId, Integer playListId) throws SongException, PlayListException;
    void removeSongFromPlayList(Long songId, Integer playListId) throws SongException, PlayListException;
    Set<Song> getPlayListTracks (Integer playListId) throws PlayListException;
    void deletePlayList(Integer playListId, Long userId) throws PlayListException, UserException;
}
