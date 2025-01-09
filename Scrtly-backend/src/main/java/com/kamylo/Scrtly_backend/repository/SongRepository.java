package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.SongEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Long> {
    @Query("select s from SongEntity s where lower(s.title) like lower(concat('%', :title, '%'))")
    Set<SongEntity> findByTitle(@Param("title") String title);

    Page<SongEntity> findByArtistId(Long artistId, Pageable pageable);

    @Query("SELECT s FROM SongEntity s JOIN s.playlists p WHERE p.id = :playlistId")
    Page<SongEntity> findByPlaylistId(Integer playlistId, Pageable pageable);

    List<SongEntity> findByAlbumId(Integer albumId);
}
