package com.kamylo.Scrtly_backend.user.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isArtist")
    private boolean isArtist;
    private boolean observed;
    private boolean isPremium;
    private int observersCount;
    private int observationsCount;
}
