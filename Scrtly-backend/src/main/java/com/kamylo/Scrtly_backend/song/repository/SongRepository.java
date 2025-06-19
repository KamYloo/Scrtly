package com.kamylo.Scrtly_backend.song.repository;

import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Long> {
    @EntityGraph(attributePaths = {"album", "artist"})
    @Query("select s from SongEntity s where lower(s.title) like lower(concat('%', :title, '%'))")
    Set<SongEntity> findByTitle(@Param("title") String title);

    @EntityGraph(attributePaths = {"album", "artist"})
    Page<SongEntity> findByArtistId(Long artistId, Pageable pageable);

    @EntityGraph(attributePaths = {"album", "artist"})
    @Query("SELECT s FROM SongEntity s JOIN s.playlists p WHERE p.id = :playlistId")
    Page<SongEntity> findByPlaylistId(Integer playlistId, Pageable pageable);

    @EntityGraph(attributePaths = {"album", "artist"})
    List<SongEntity> findByAlbumId(Integer albumId);
}
