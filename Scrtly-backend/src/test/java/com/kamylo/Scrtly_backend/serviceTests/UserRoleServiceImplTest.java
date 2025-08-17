package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.user.domain.RoleEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.user.service.impl.UserRoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    private UserEntity userWithArtistRole;
    private UserEntity userWithAdminRole;
    private UserEntity userWithBothRoles;
    private UserEntity userWithNoRoles;

    private static RoleEntity role(String name) {
        return RoleEntity.builder().name(name).build();
    }

    @BeforeEach
    void setUp() {
        userWithArtistRole = new UserEntity();
        userWithArtistRole.setRoles(Set.of(role("ARTIST")));

        userWithAdminRole = new UserEntity();
        userWithAdminRole.setRoles(Set.of(role("ADMIN")));

        userWithBothRoles = new UserEntity();
        userWithBothRoles.setRoles(Set.of(role("ADMIN"), role("ARTIST")));

        userWithNoRoles = new UserEntity();
        userWithNoRoles.setRoles(Collections.emptySet());
    }

    @Test
    void isArtist_shouldReturnTrue_whenUserHasArtistRole() {
        when(userService.findUserByEmail("artist@example.com")).thenReturn(userWithArtistRole);
        assertTrue(userRoleService.isArtist("artist@example.com"));
    }

    @Test
    void isArtist_shouldReturnFalse_whenUserDoesNotHaveArtistRole() {
        when(userService.findUserByEmail("admin@example.com")).thenReturn(userWithAdminRole);
        assertFalse(userRoleService.isArtist("admin@example.com"));
    }

    @Test
    void isAdmin_shouldReturnTrue_whenUserHasAdminRole() {
        when(userService.findUserByEmail("admin@example.com")).thenReturn(userWithAdminRole);
        assertTrue(userRoleService.isAdmin("admin@example.com"));
    }

    @Test
    void isAdmin_shouldReturnFalse_whenUserDoesNotHaveAdminRole() {
        when(userService.findUserByEmail("artist@example.com")).thenReturn(userWithArtistRole);
        assertFalse(userRoleService.isAdmin("artist@example.com"));
    }

    @Test
    void isArtist_shouldReturnTrue_whenUserHasMultipleRoles() {
        when(userService.findUserByEmail("both@example.com")).thenReturn(userWithBothRoles);
        assertTrue(userRoleService.isArtist("both@example.com"));
    }

    @Test
    void isAdmin_shouldReturnTrue_whenUserHasMultipleRoles() {
        when(userService.findUserByEmail("both@example.com")).thenReturn(userWithBothRoles);
        assertTrue(userRoleService.isAdmin("both@example.com"));
    }

    @Test
    void isArtist_shouldReturnFalse_whenUserHasNoRoles() {
        when(userService.findUserByEmail("norole@example.com")).thenReturn(userWithNoRoles);
        assertFalse(userRoleService.isArtist("norole@example.com"));
    }

    @Test
    void isAdmin_shouldReturnFalse_whenUserHasNoRoles() {
        when(userService.findUserByEmail("norole@example.com")).thenReturn(userWithNoRoles);
        assertFalse(userRoleService.isAdmin("norole@example.com"));
    }

    @Test
    void isArtist_shouldBeCaseInsensitive() {
        UserEntity user = new UserEntity();
        user.setRoles(Set.of(role("artist")));
        when(userService.findUserByEmail("case@example.com")).thenReturn(user);
        assertTrue(userRoleService.isArtist("case@example.com"));
    }

    @Test
    void isAdmin_shouldBeCaseInsensitive() {
        UserEntity user = new UserEntity();
        user.setRoles(Set.of(role("admin")));
        when(userService.findUserByEmail("case@example.com")).thenReturn(user);
        assertTrue(userRoleService.isAdmin("case@example.com"));
    }

    @Test
    void isArtist_shouldThrowException_whenUserServiceThrows() {
        when(userService.findUserByEmail("fail@example.com")).thenThrow(new RuntimeException("User not found"));
        assertThrows(RuntimeException.class, () -> userRoleService.isArtist("fail@example.com"));
    }

    @Test
    void isAdmin_shouldThrowException_whenUserServiceThrows() {
        when(userService.findUserByEmail("fail@example.com")).thenThrow(new RuntimeException("User not found"));
        assertThrows(RuntimeException.class, () -> userRoleService.isAdmin("fail@example.com"));
    }
}