package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.AlbumEntity;
import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    @EntityGraph(attributePaths = {"user"})
    Set<ArtistEntity> findByPseudonym(@Param("pseudonym") String pseudonym);

    @EntityGraph(attributePaths = {"user"})
    Page<ArtistEntity> findAll(Pageable pageable);
}
