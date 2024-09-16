package com.kamylo.Scrtly_backend.request;

import com.kamylo.Scrtly_backend.model.Artist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlbumRequest {
    private String title;
    private String coverImage;
    private Artist artist;
}
