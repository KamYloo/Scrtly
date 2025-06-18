package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.entity.*;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.repository.RolesRepository;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.service.ArtistVerificationTokenService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private ArtistVerificationTokenService artistVerificationTokenService;
    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private UserEntity user;
    private ArtistVerificationToken token;
    private RoleEntity artistRole;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(1L)
                .roles(new HashSet<>())
                .build();

        token = ArtistVerificationToken.builder()
                .token("valid-token")
                .requestedArtistName("Pseudonym")
                .build();

        artistRole = RoleEntity.builder()
                .id(2L)
                .name("ARTIST")
                .build();
    }

    @Test
    void shouldThrowIfUserAlreadyArtist() {
        user.setRoles(new HashSet<>(Set.of(RoleEntity.builder().name("ARTIST").build())));
        when(userService.findUserById(1L)).thenReturn(user);

        Throwable thrown = catchThrowable(() -> adminService.verifyUserAsArtist(1L, "any-token"));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(BusinessErrorCodes.USER_ALREADY_ARTIST);

        verifyNoInteractions(artistVerificationTokenService, rolesRepository, userRepository);
    }

    @Test
    void shouldThrowIfTokenInvalid() {
        when(userService.findUserById(1L)).thenReturn(user);
        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(token);

        Throwable thrown = catchThrowable(() -> adminService.verifyUserAsArtist(1L, "wrong-token"));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(BusinessErrorCodes.INVALID_TOKEN);

        verify(artistVerificationTokenService, never()).tokenExpired(any());
        verifyNoInteractions(rolesRepository, userRepository);
    }

    @Test
    void shouldThrowIfTokenExpired() {
        when(userService.findUserById(1L)).thenReturn(user);
        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(token);
        when(artistVerificationTokenService.tokenExpired(token)).thenReturn(true);

        Throwable thrown = catchThrowable(() -> adminService.verifyUserAsArtist(1L, "valid-token"));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(BusinessErrorCodes.TOKEN_EXPIRED);

        verify(artistVerificationTokenService).deleteToken(token);
        verifyNoInteractions(rolesRepository, userRepository);
    }

    @Test
    void shouldThrowIfArtistRoleNotFound() {
        when(userService.findUserById(1L)).thenReturn(user);
        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(token);
        when(artistVerificationTokenService.tokenExpired(token)).thenReturn(false);
        when(rolesRepository.findByName("ARTIST")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.verifyUserAsArtist(1L, "valid-token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found");
    }

    @Test
    void shouldVerifyUserAsArtistSuccessfully() {
        when(userService.findUserById(1L)).thenReturn(user);
        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(token);
        when(artistVerificationTokenService.tokenExpired(token)).thenReturn(false);
        when(rolesRepository.findByName("ARTIST")).thenReturn(Optional.of(artistRole));

        adminService.verifyUserAsArtist(1L, "valid-token");

        assertThat(user.getRoles()).contains(artistRole);
        assertThat(user.getArtistEntity()).isNotNull();
        assertThat(user.getArtistEntity().getPseudonym()).isEqualTo("Pseudonym");

        verify(userRepository).save(user);
        verify(artistVerificationTokenService).deleteToken(token);
    }
}