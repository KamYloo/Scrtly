package com.kamylo.Scrtly_backend.like.repository;

import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.like.domain.SongLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongLikeRepository extends JpaRepository<SongLikeEntity, Long> {
    SongLikeEntity findByUserIdAndSongId(Long userId, Long songId);
    void deleteBySong(SongEntity songEntity);
}
