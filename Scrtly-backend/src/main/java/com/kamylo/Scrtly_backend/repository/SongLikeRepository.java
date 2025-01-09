package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.SongEntity;
import com.kamylo.Scrtly_backend.entity.SongLikeEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongLikeRepository extends JpaRepository<SongLikeEntity, Long> {
    SongLikeEntity findByUserIdAndSongId(Long userId, Long songId);
    void deleteBySong(SongEntity songEntity);
}
