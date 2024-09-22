package com.kamylo.Scrtly_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private LocalDate releaseDate;
    private String coverImage;

    @ManyToOne
    @ToString.Exclude
    private Artist artist;

    @OneToMany(mappedBy = "album", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Song> songs = new ArrayList<>();
}
