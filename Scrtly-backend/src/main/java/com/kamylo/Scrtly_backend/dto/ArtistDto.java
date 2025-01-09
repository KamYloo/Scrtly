package com.kamylo.Scrtly_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDto {
    private Long id;
    private String artistName;
    private String bannerImg;
    private String artistBio;
    private String profilePicture;
    private boolean observed;
    private int totalFans;
}
