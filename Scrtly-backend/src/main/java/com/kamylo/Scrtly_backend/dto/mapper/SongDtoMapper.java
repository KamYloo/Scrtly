package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SongDtoMapper {
    public static SongDto toSongDto(Song song, User reqUser) {
        ArtistDto artist = ArtistDtoMapper.toArtistDto(song.getArtist(), reqUser);
        AlbumDto album = AlbumDtoMapper.toAlbumDto(song.getAlbum(),reqUser);
        SongDto songDto = new SongDto();
        songDto.setId(song.getId());
        songDto.setTitle(song.getTitle());
        songDto.setArtist(artist);
        songDto.setAlbum(album);
        songDto.setImageSong(song.getImageSong());
        songDto.setDuration(song.getDuration());
        songDto.setFavorite(song.isFavorite());
        songDto.setTrack(song.getTrack());
        return songDto;
    }

    public static List<SongDto> toSongDtoListArrayList(List<Song> songs, User reqUser) {
        List<SongDto> songDtos = new ArrayList<>();
        for (Song song : songs) {
            songDtos.add(toSongDto(song, reqUser));
        }
        return songDtos;
    }

    public static Set<SongDto> toSongDtoListHashSet(Set<Song> songs, User reqUser) {
        Set<SongDto> songDtos = new HashSet<>();
        for (Song song : songs) {
            songDtos.add(toSongDto(song, reqUser));
        }
        return songDtos;
    }
}
