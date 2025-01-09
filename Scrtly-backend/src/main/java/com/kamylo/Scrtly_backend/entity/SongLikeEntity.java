package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "songLike")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SongLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private SongEntity song;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongLikeEntity songLike = (SongLikeEntity) o;
        return Objects.equals(id, songLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
