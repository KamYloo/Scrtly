package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotBlank(message = "Album title must not be blank")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @CreatedDate
    @NotNull(message = "Release date must not be null")
    private LocalDate releaseDate;

    @NotBlank(message = "Cover image must not be blank")
    @Size(max = 255, message = "Cover image URL must be at most 255 characters")
    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private UserEntity artist;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongEntity> songs = new ArrayList<>();
}
