package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String image;
    private LocalDateTime timestamp;

    @ManyToOne
    @ToString.Exclude
    private User user;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

}
