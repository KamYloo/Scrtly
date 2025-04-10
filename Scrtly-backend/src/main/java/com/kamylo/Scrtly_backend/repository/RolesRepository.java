package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RolesRepository extends CrudRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
