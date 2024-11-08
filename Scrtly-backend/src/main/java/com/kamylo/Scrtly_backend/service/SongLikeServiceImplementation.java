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
        SongLike existingLike = songLikeRepository.findByUserAndSong(user, song).orElse(null);

        if (existingLike != null) {
            song.setFavorite(false);
            playListService.removeFromFavourites(user, song);
            songLikeRepository.delete(existingLike);
        } else {
            song.setFavorite(true);
            playListService.addToFavourites(user, song);
            SongLike newLike = new SongLike();
            newLike.setSong(song);
            newLike.setUser(user);
            return songLikeRepository.save(newLike);
        }

        return null;
    }
}
