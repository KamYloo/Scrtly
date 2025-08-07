package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.auth.service.impl.CustomOAuth2UserServiceImpl;
import com.kamylo.Scrtly_backend.user.domain.RoleEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.RolesRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceImplTest {

    @Mock
    private OidcUserService delegate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    private CustomOAuth2UserServiceImpl service;

    @Mock
    private OidcUserRequest userRequest;

    @Mock
    private OidcUser oidcUser;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @Test
    void loadUser_existingUser_shouldNotSaveNewUser() throws OAuth2AuthenticationException {
        String email = "exists@example.com";
        when(delegate.loadUser(userRequest)).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));

        OidcUser result = service.loadUser(userRequest);

        assertThat(result).isSameAs(oidcUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    void loadUser_newUser_shouldRegisterAndSave() throws OAuth2AuthenticationException {
        String email = "new@example.com";
        String fullName = "New User";
        String picture = "url-to-picture";
        RoleEntity role = new RoleEntity(1L, "USER");

        when(delegate.loadUser(userRequest)).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(email);
        when(oidcUser.getFullName()).thenReturn(fullName);
        when(oidcUser.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(rolesRepository.findByName("USER")).thenReturn(Optional.of(role));

        OidcUser result = service.loadUser(userRequest);

        assertThat(result).isSameAs(oidcUser);
        verify(userRepository).save(userCaptor.capture());

        UserEntity saved = userCaptor.getValue();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getFullName()).isEqualTo(fullName);
        assertThat(saved.getNickName()).isEqualTo(email.split("@")[0]);
        assertThat(saved.getProfilePicture()).isEqualTo(picture);
        assertThat(saved.getRoles()).hasSize(1).containsExactly(role);
        assertThat(saved.isEnable()).isTrue();
        assertThat(saved.isAccountLocked()).isFalse();
    }

    @Test
    void loadUser_delegateThrows_shouldPropagate() {
        when(delegate.loadUser(userRequest)).thenThrow(new OAuth2AuthenticationException("err"));
        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(userRequest));
    }
}
