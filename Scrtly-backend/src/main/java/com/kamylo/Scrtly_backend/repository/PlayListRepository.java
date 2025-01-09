package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.PlayListEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayListRepository extends JpaRepository<PlayListEntity, Integer> {
    Page<PlayListEntity> getPlayListsByUserId(Long userId, Pageable pageable);
    Optional<PlayListEntity> findByUserIdAndFavourite(Long userId, boolean isFavourite);
}
