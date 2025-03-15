package com.kamylo.Scrtly_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayListDto {
    private Integer id;
    private String title;
    private boolean favourite;
    private LocalDate creationDate;
    private String coverImage;
    private UserDto user;
    private int tracksCount;
    private int totalDuration;
}
