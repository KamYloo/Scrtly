package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.*;
import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.SongLike;
import com.kamylo.Scrtly_backend.model.User;

public class SongLikeDtoMapper {

    public static SongLikeDto toLikeSongDto(SongLike like, User reqUser) {
        UserDto user = UserDtoMapper.toUserDto(like.getUser());
        SongDto songDto = SongDtoMapper.toSongDto(like.getSong(), reqUser);
        SongLikeDto likeDto = new SongLikeDto();
        likeDto.setUser(user);
        likeDto.setSong(songDto);
        likeDto.setId(like.getId());

        return likeDto;
    }
}
