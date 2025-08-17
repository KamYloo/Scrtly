package com.kamylo.Scrtly_backend.like.web.dto;

import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongLikeDto {
    private Long id;
    private UserDto user;
    private SongDto song;
}
