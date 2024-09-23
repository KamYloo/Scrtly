package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;

import java.util.ArrayList;
import java.util.List;

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
        songDto.setTrack(song.getTrack());
        return songDto;
    }

    public static List<SongDto> toSongDtoList(List<Song> songs, User reqUser) {
        List<SongDto> songDtos = new ArrayList<>();
        for (Song song : songs) {
            songDtos.add(toSongDto(song, reqUser));
        }
        return songDtos;
    }
}
