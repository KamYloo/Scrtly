package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.entity.RoleEntity;
import com.kamylo.Scrtly_backend.repository.RolesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RolesRepositoryIntegrationTest {

    @Autowired
    private RolesRepository rolesRepository;

    @Test
    void findByName_shouldReturnRoleEntity_whenRoleExists() {
        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        rolesRepository.save(role);

        Optional<RoleEntity> result = rolesRepository.findByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    @Test
    void findByName_shouldReturnEmptyOptional_whenRoleDoesNotExist() {
        Optional<RoleEntity> result = rolesRepository.findByName("USER");
        assertFalse(result.isPresent());
    }
}