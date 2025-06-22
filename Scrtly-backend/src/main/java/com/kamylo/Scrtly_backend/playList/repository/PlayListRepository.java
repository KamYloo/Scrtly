package com.kamylo.Scrtly_backend.playList.repository;

import com.kamylo.Scrtly_backend.playList.domain.PlayListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayListRepository extends JpaRepository<PlayListEntity, Integer> {
    @EntityGraph(attributePaths = {"user"})
    Page<PlayListEntity> getPlayListsByUserId(Long userId, Pageable pageable);
    @EntityGraph(attributePaths = {"user"})
    Optional<PlayListEntity> findByUserIdAndFavourite(Long userId, boolean isFavourite);
}
