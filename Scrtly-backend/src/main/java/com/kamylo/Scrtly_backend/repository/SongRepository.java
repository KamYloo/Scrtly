package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    @Query("select s from Song s where lower(s.title) like lower(concat('%', :title, '%'))")
    Set<Song> findByTitle(@Param("title") String title);
}
