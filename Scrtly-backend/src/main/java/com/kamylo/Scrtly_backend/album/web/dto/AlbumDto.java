package com.kamylo.Scrtly_backend.album.web.dto;

import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDto {
    private Integer id;
    private String title;
    private LocalDate releaseDate;
    private String albumImage;
    private ArtistDto artist;
    private int tracksCount;
    private int totalDuration;
}
