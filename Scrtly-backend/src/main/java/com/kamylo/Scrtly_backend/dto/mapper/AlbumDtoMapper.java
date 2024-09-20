package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumDtoMapper {
    public static AlbumDto toAlbumDto(Album album) {
        ArtistDto artist = ArtistDtoMapper.toArtistDto(album.getArtist());
        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(album.getId());
        albumDto.setAlbumImage(album.getCoverImage());
        albumDto.setArtist(artist);
        albumDto.setTitle(album.getTitle());
        albumDto.setReleaseDate(album.getReleaseDate());
        return albumDto;
    }
    public static List<AlbumDto> toAlbumDtos(List<Album> albums) {
        List<AlbumDto> albumDtos = new ArrayList<>();
        for (Album album : albums) {
            albumDtos.add(toAlbumDto(album));
        }
        return albumDtos;
    }
}
