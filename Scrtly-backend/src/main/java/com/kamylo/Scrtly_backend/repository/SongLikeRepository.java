package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.SongLike;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongLikeRepository extends JpaRepository<SongLike, Long> {
    Optional<SongLike> findByUserAndSong(User user, Song song);
    void deleteBySong(Song song);
}
