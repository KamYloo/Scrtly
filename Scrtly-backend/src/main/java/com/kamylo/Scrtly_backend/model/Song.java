package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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
    private String imageSong;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    @ToString.Exclude
    private Album album;

    @ManyToOne
    @ToString.Exclude
    private Artist artist;

//    @ManyToMany(mappedBy = "songs")
   // @ToString.Exclude
//    private Set<Playlist> playlists = new HashSet<>();
}
