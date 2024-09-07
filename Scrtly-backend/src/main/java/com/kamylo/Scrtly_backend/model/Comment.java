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
    private Post post;

    @ManyToOne
    private User user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private Set<Like> likes =  new HashSet<>();

    @JsonProperty("likesCount")
    public int getLikesCount() {
        return likes.size();
    }

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

}
