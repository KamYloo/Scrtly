package com.kamylo.Scrtly_backend.dto;

import com.kamylo.Scrtly_backend.model.Artist;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AlbumDto {
    private Integer id;
    private String title;
    private LocalDate releaseDate;
    private String albumImage;

    private ArtistDto artist;
    private boolean isReqArtistAlbum;
}
