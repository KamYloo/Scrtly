package com.kamylo.Scrtly_backend.artist.repository;

import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistVerificationTokenRepository extends CrudRepository<ArtistVerificationToken, Long> {
    Optional<ArtistVerificationToken> findByUser(UserEntity user);
}