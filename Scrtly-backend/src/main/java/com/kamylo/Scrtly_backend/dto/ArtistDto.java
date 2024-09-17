package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

@Data
public class ArtistDto {
    private Long id;
    private String artistName;
    private String bannerImg;
    private String artistBio;
    private String artistPic;

    private boolean req_artist;
}
