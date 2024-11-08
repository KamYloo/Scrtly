package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.PlayListException;
import com.kamylo.Scrtly_backend.exception.SongException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.PlayListRepository;
import com.kamylo.Scrtly_backend.request.PlayListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlayListServiceImplementation implements PlayListService {
    @Autowired
    private UserService userService;

    @Autowired
    private FileServiceImplementation fileService;

    @Autowired
    private PlayListRepository playListRepository;

    @Autowired
    private SongService songService;

    @Override
    public PlayList createPlayList(PlayListRequest playListRequest, MultipartFile playListImage) throws UserException {
        User user = userService.findUserById(playListRequest.getUser().getId());
        PlayList playList = new PlayList();
        playList.setUser(user);
        playList.setTitle(playListRequest.getTitle());
        if (!playListImage.isEmpty()) {
            String imagePath = fileService.saveFile(playListImage, "/uploads/playListImages");
            playList.setCoverImage("/uploads/playListImages/" + imagePath);
        }
        playList.setCreationDate(LocalDate.now());
        return playListRepository.save(playList);
    }

    @Override
    public PlayList getPlayList(Integer playListId) throws PlayListException {
        return playListRepository.findById(playListId).orElseThrow(() -> new PlayListException("PlayList not found with id: " + playListId));
    }

    @Override
    public List<PlayList> getPlayLists() {
        return playListRepository.findAllByOrderByIdDesc();
    }

    @Override
    public List<PlayList> getPlayListsByUser(Long userId) throws UserException {
        User user = userService.findUserById(userId);
        List<PlayList> playLists = user.getPlaylists();
        return (playLists != null) ? playLists : new ArrayList<>();
    }

    @Override
    public PlayList addSongToPlayList(Long songId, Integer playListId) throws SongException, PlayListException {
        Song song = songService.findSongById(songId);
        PlayList playList = getPlayList(playListId);
        if (playList.getSongs().contains(song)) {
            throw new SongException("Song already exists");
        }
        playList.getSongs().add(song);
        return playListRepository.save(playList);
    }

    @Override
    public void addToFavourites(User user, Song song) {
        PlayList favourites = findOrCreateFavouritePlayList(user);
        if (!favourites.getSongs().contains(song)) {
            favourites.getSongs().add(song);
            playListRepository.save(favourites);
        }
    }

    @Override
    public void removeFromFavourites(User user, Song song) {
        PlayList favourites = findOrCreateFavouritePlayList(user);
        favourites.getSongs().remove(song);
        playListRepository.save(favourites);
    }

    @Override
    public void removeSongFromPlayList(Long songId, Integer playListId) throws SongException, PlayListException {
        Song song = songService.findSongById(songId);
        PlayList playList = getPlayList(playListId);
        if(playList.getSongs().contains(song)) {
            playList.getSongs().remove(song);
        }
        else {
            throw new SongException("Song does not exist");
        }
        playListRepository.save(playList);
    }

    @Override
    public Set<Song> getPlayListTracks(Integer playListId) throws PlayListException {
        PlayList playList = getPlayList(playListId);
        Set<Song> songs = playList.getSongs();
        return (songs != null) ? songs : new HashSet<>();
    }

    @Override
    public void deletePlayList(Integer playListId, Long userId) throws PlayListException, UserException {
        PlayList playList = getPlayList(playListId);
        if (playList == null) {
            throw new PlayListException("PlayList not found with id: " + playListId);
        }
        if (!userId.equals(playList.getUser().getId())) {
            throw new UserException("You do not have permission to delete this playlist");
        }
        playListRepository.deleteById(playListId);
        fileService.deleteFile(playList.getCoverImage());
    }


    private PlayList findOrCreateFavouritePlayList(User user) {
        return playListRepository.findByUserAndFavourite(user, true)
                .orElseGet(() -> {
                    PlayList favouritePlayList = new PlayList();
                    favouritePlayList.setUser(user);
                    favouritePlayList.setFavourite(true);
                    favouritePlayList.setTitle("Favourite Songs");
                    return playListRepository.save(favouritePlayList);
                });
    }
}
