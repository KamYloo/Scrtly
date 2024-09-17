package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ARTIST")
@EqualsAndHashCode(callSuper = true)
@Data
public class Artist extends User {

    private String artistName;
    private String bannerImg;
    private String artistBio;

    @OneToMany(mappedBy = "artist")
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<Song> songs = new ArrayList<>();
}
