package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArtistVerificationTokenRepository extends CrudRepository<ArtistVerificationToken, Long> {
    Optional<ArtistVerificationToken> findByUser(UserEntity user);
}