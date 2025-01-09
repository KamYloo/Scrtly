package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Integer>, JpaSpecificationExecutor<AlbumEntity> {
    List<AlbumEntity> findByArtistId(Long artistId);
}
