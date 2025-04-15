package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.ActivationToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends CrudRepository<ActivationToken, Long> {
    Optional<ActivationToken> findByToken(String token);
    Optional<ActivationToken> findByUser(UserEntity user);
}
