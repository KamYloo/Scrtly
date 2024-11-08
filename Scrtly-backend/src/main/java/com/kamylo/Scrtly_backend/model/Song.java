package com.kamylo.Scrtly_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "songs")
@Data
@EqualsAndHashCode(exclude = {"playlists", "artist", "album"})
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String track;
    private int duration;
    private String imageSong;
    private boolean favorite;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    @JsonBackReference
    private Album album;

    @ManyToOne
    @JsonBackReference
    private Artist artist;

    @ManyToMany(mappedBy = "songs")
    @ToString.Exclude
    private Set<PlayList> playlists = new HashSet<>();
}
