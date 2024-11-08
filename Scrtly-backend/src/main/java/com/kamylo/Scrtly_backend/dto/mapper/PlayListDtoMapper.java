package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.PlayListDto;
import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayListDtoMapper {
    public static PlayListDto toPlayListDto(PlayList playList) {
        UserDto user = UserDtoMapper.toUserDto(playList.getUser());
        PlayListDto playListDto = new PlayListDto();
        playListDto.setId(playList.getId());
        playListDto.setUser(user);
        playListDto.setFavourite(playList.isFavourite());
        playListDto.setCreationDate(playList.getCreationDate());
        playListDto.setPlayListImage(playList.getCoverImage());
        playListDto.setTitle(playList.getTitle());
        playListDto.setTotalSongs(playList.getSongs().size());
        int totalDuration = playList.getSongs().stream().mapToInt(Song::getDuration).sum();
        playListDto.setTotalDuration(totalDuration);
        return playListDto;
    }

    public static List<PlayListDto> toPlayListDtos(List<PlayList> playLists) {
        List<PlayListDto> playListDtos = new ArrayList<>();
        for (PlayList playList:playLists ) {
            playListDtos.add(toPlayListDto(playList));
        }
        return playListDtos;
    }
}
