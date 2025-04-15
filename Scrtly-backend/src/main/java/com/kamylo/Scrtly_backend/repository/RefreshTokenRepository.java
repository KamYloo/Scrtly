package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    Optional<RefreshTokenEntity> findByUser(UserEntity user);
    Optional<RefreshTokenEntity> findByTokenId(String tokenId);

    @Modifying
    void deleteByUser(UserEntity user);
}
