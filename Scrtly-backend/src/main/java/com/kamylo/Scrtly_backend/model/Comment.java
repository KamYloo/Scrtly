package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private LocalDateTime creationDate;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private Set<User> likes =  new HashSet<>();

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

}
