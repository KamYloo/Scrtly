package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@Entity
@Data
public class SongLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private User user;

    @ManyToOne
    @ToString.Exclude
    private Song song;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongLike songLike = (SongLike) o;
        return Objects.equals(id, songLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
