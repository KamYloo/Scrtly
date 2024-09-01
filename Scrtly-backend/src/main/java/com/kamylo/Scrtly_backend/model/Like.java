package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "user_likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment comment;
}
