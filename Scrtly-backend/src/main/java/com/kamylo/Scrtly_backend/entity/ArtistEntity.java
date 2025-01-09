package com.kamylo.Scrtly_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "artist")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@DiscriminatorValue("ARTIST")
public class ArtistEntity extends UserEntity {

    private String artistName;
    private String bannerImg;
    private String artistBio;

    @JsonIgnore
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlbumEntity> albums = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongEntity> songs = new ArrayList<>();
}
