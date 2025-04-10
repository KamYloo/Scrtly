package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
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

    private String title;

    @CreatedDate
    private LocalDate releaseDate;

    private String coverImage;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private UserEntity artist;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongEntity> songs = new ArrayList<>();
}
