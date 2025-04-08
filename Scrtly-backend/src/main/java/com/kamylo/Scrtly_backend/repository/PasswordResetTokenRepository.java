package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.PasswordResetToken;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    PasswordResetToken findByUser(UserEntity user);
}
