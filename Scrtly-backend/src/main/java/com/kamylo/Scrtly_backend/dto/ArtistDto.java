package com.kamylo.Scrtly_backend.dto;

import com.kamylo.Scrtly_backend.model.User;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ArtistDto {
    private Long id;
    private String artistName;
    private String bannerImg;
    private String artistBio;
    private String artistPic;
    private boolean req_artist;
    private boolean followed;
    private int totalFans;
    private Set<UserDto> fans = new HashSet<>();
}
