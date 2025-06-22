package com.kamylo.Scrtly_backend.auth.service.impl;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.RolesRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserServiceImpl  implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(request);
        String email = oidcUser.getEmail();
        userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(oidcUser));
        return oidcUser;
    }

    private UserEntity registerNewUser(OidcUser oidc) {
        UserEntity user = UserEntity.builder()
                .email(oidc.getEmail())
                .fullName(oidc.getFullName())
                .nickName(oidc.getEmail().split("@")[0])
                .profilePicture(oidc.getAttribute("picture"))
                .password("")
                .roles(Set.of(rolesRepository.findByName("USER").orElseThrow()))
                .enable(true)
                .accountLocked(false)
                .build();
        return userRepository.save(user);
    }
}
