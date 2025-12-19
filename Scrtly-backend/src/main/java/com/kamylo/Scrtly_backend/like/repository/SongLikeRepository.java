package com.kamylo.Scrtly_backend.like.repository;

import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.like.domain.SongLikeEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface SongLikeRepository extends JpaRepository<SongLikeEntity, Long> {
    SongLikeEntity findByUserIdAndSongId(Long userId, Long songId);
    void deleteBySong(SongEntity songEntity);

    @Query("SELECT sl.song.id FROM SongLikeEntity sl WHERE sl.user.id = :userId AND sl.song.id IN :songIds")
    Set<Long> findSongIdsLikedByUser(@Param("userId") Long userId, @Param("songIds") List<Long> songIds);

    @Modifying
    @Query("DELETE FROM SongLikeEntity sl WHERE sl.user = :user AND sl.song IN :songs")
    void deleteByUserAndSongs(@Param("user") UserEntity user, @Param("songs") Collection<SongEntity> songs);
}
