package com.kamylo.Scrtly_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayListDto {
    private Integer id;
    private String title;
    private boolean favourite;
    private LocalDate creationDate;
    private String playListImage;
    private UserDto user;
    private int totalSongs;
    private int totalDuration;
}
