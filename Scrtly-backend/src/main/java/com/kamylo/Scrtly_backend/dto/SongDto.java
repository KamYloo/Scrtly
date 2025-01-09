package com.kamylo.Scrtly_backend.dto;

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
    private int duration;
    private boolean favorite;
    private String imageSong;
    private ArtistDto artist;
    private AlbumDto album;
}
