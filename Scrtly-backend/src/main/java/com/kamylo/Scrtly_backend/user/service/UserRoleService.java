package com.kamylo.Scrtly_backend.user.service;

public interface UserRoleService {
    boolean isArtist(String username);
    boolean isAdmin(String username);
}
