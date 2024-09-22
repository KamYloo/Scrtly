package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

@Data
public class SongDto {
    private Long id;
    private String title;
    private String track;
    private int duration;
    private String imageSong;
    private ArtistDto artist;
    private AlbumDto album;
}
