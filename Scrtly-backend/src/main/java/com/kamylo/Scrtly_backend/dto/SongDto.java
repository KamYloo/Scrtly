package com.kamylo.Scrtly_backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SongDto {
    private Long id;
    private String title;
    private String track;
    private String hlsManifestUrl;
    private String contentType;
    private int duration;
    private boolean favorite;
    private String imageSong;
    private ArtistDto artist;
    private AlbumDto album;
}
