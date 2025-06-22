package com.kamylo.Scrtly_backend.auth.repository;

import com.kamylo.Scrtly_backend.auth.domain.PasswordResetToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    PasswordResetToken findByUser(UserEntity user);
}
