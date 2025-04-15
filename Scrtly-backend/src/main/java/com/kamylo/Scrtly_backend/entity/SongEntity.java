package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "songs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank(message = "Track must not be blank")
    @Size(max = 255, message = "Track path cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String track;

    private int duration;

    @NotBlank(message = "Image must not be blank")
    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
    @Column(name = "image_song", nullable = false, length = 255)
    private String imageSong;

    private boolean favorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private AlbumEntity album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private UserEntity artist;

    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY)
    private Set<PlayListEntity> playlists = new HashSet<>();
}
