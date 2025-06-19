package com.kamylo.Scrtly_backend.user.service;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserService userService;

    @Override
    public boolean isArtist(String username) {
        return hasRole(username, "ARTIST");
    }

    @Override
    public boolean isAdmin(String username) {
        return hasRole(username, "ADMIN");
    }

    private boolean hasRole(String username, String role) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(role));
    }

}
