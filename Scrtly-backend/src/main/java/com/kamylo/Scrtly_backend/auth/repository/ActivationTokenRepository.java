package com.kamylo.Scrtly_backend.auth.repository;

import com.kamylo.Scrtly_backend.auth.domain.ActivationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends CrudRepository<ActivationToken, Long> {
    Optional<ActivationToken> findByToken(String token);
    Optional<ActivationToken> findByUser(UserEntity user);
}
