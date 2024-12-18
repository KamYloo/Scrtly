package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Data
@Table(name = "user_likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private User user;

    @ManyToOne
    @ToString.Exclude
    private Post post;

    @ManyToOne
    @ToString.Exclude
    private Comment comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(id, like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
