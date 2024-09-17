package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.model.Artist;

import java.util.ArrayList;
import java.util.List;

public class ArtistDtoMapper {

    public static ArtistDto toArtistDto(Artist artist) {
        ArtistDto artistDto = new ArtistDto();
        artistDto.setId(artist.getId());
        artistDto.setArtistName(artist.getArtistName());
        artistDto.setArtistBio(artist.getArtistBio());
        artistDto.setArtistPic(artist.getProfilePicture());
        artistDto.setBannerImg(artist.getBannerImg());
        return artistDto;
    }

    public static List<ArtistDto> toArtistDtos(List<Artist> artists) {
        List<ArtistDto> artistDtos = new ArrayList<>();
        for (Artist artist : artists) {
            artistDtos.add(toArtistDto(artist));
        }
        return artistDtos;
    }
}
