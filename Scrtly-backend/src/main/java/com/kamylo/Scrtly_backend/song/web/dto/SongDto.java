package com.kamylo.Scrtly_backend.song.web.dto;

import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
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
    private Long playCount;
    private boolean favorite;
    private String imageSong;
    private ArtistDto artist;
    private AlbumDto album;
}
