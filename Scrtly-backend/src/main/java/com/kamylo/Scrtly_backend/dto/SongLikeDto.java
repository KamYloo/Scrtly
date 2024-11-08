package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

@Data
public class SongLikeDto {
    private Long id;
    private UserDto user;
    private SongDto song;
}
