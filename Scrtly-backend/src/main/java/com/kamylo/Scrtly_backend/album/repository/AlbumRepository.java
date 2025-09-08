package com.kamylo.Scrtly_backend.album.repository;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Integer>, JpaSpecificationExecutor<AlbumEntity> {
    @EntityGraph(attributePaths = {"artist"})
    List<AlbumEntity> findByArtistId(Long artistId);

    @EntityGraph(attributePaths = {"artist"})
    Page<AlbumEntity> findByArtistId(Long artistId, Pageable pageable);

    @EntityGraph(attributePaths = {"artist"})
    Page<AlbumEntity> findByArtistIdAndTitleIgnoreCaseContaining(Long artistId, String title, Pageable pageable);

    @EntityGraph(attributePaths = {"artist"})
    Page<AlbumEntity> findAll(Specification<AlbumEntity> spec, Pageable pageable);
}
