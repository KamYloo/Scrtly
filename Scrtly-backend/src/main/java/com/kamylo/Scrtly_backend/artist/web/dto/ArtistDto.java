package com.kamylo.Scrtly_backend.artist.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDto {
    private Long id;
    private String pseudonym;
    private String bannerImg;
    private String artistBio;
    private String profilePicture;
    private boolean observed;
    private int totalFans;
    private int monthlyPlays;
}
