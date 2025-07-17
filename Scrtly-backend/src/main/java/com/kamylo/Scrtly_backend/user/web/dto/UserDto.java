package com.kamylo.Scrtly_backend.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String nickName;
    private String profilePicture;
    private String description;

    private boolean observed;
    private boolean isPremium;
    private int observersCount;
    private int observationsCount;

    /*private Set<UserDto> followers = new HashSet<>();
    private Set<UserDto> following = new HashSet<>();*/
}
