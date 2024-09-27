package com.kamylo.Scrtly_backend.request;

import com.kamylo.Scrtly_backend.model.Artist;
import lombok.*;

@Data
public class AlbumRequest {
    private String title;
    private Artist artist;
}
