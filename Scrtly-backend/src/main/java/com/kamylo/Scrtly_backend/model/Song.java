package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;

@Entity
@Table(name = "songs")
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int duration;
    private String genre;
    private String imageSong;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne
    private Artist artist;

//    @ManyToMany(mappedBy = "songs")
//    private Set<Playlist> playlists = new HashSet<>();
}
