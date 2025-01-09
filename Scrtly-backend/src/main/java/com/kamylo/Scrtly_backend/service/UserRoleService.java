package com.kamylo.Scrtly_backend.service;

public interface UserRoleService {
    boolean isArtist(String username);
    boolean isAdmin(String username);
}
