package com.kamylo.Scrtly_backend.dto;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String fullName;
    private String profilePicture;
    private String description;
    private String role;

    private boolean req_user;
    private boolean followed;

    private int totalFollowers;
    private int totalFollowing;
    private Set<UserDto> followers = new HashSet<>();
    private Set<UserDto> following = new HashSet<>();

}
