package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.SongLike;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.SongLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SongLikeServiceImplementation implements SongLikeService {
    @Autowired
    private SongLikeRepository songLikeRepository;

    @Autowired
    private PlayListService playListService;

    @Override
    public SongLike likeSong(Song song, User user) {
        SongLike songLike = songLikeRepository.findByUserAndSong(user, song)
                .orElseGet(() -> {
                    SongLike like = new SongLike();
                    like.setSong(song);
                    like.setUser(user);
                    playListService.addToFavourites(user, song);
                    return songLikeRepository.save(like);
                });
        playListService.removeFromFavourites(user, song);
        songLikeRepository.delete(songLike);
        return songLike;
    }
}
