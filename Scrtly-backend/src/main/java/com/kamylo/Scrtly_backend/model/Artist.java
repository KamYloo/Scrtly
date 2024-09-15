package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ARTIST")
@EqualsAndHashCode(callSuper = true)
public class Artist extends User {

    private String artistName;
    private String genre;

    @OneToMany(mappedBy = "artist")
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<Song> songs = new ArrayList<>();
}
