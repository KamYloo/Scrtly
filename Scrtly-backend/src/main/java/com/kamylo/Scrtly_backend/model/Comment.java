package com.kamylo.Scrtly_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
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
    @ToString.Exclude
    private Post post;

    @ManyToOne
    @ToString.Exclude
    private User user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Like> likes =  new HashSet<>();


    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

}
