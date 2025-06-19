package com.kamylo.Scrtly_backend.song.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SongRequest {
    private String title;
    private Integer albumId;
}
