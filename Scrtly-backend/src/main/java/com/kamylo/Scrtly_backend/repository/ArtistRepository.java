package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    @Query("select a from ArtistEntity a where a.fullName like %:artistName%")
    Set<ArtistEntity> findByArtistName(@Param("artistName") String artistName);
}
