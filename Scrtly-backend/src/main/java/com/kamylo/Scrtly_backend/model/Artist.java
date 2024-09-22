package com.kamylo.Scrtly_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    @JsonIgnore
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Album> albums = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Song> songs = new ArrayList<>();
}
