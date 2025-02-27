package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "songs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(exclude = {"playlists", "artist", "album"})
public class SongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String title;
    private String track;
    private int duration;
    private String imageSong;
    private boolean favorite;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private AlbumEntity album;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @ManyToMany(mappedBy = "songs")
    private Set<PlayListEntity> playlists = new HashSet<>();

}
