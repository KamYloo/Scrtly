package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.service.UserRoleService;
import com.kamylo.Scrtly_backend.service.UserService;
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
        user.getAuthorities().forEach(auth -> System.out.println("Authority: " + auth.getAuthority()));
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(role));
    }

}
