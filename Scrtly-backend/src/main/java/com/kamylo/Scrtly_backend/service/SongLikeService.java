package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.SongLike;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.stereotype.Service;

@Service
public interface SongLikeService {
    SongLike likeSong(Song song, User user);
}
