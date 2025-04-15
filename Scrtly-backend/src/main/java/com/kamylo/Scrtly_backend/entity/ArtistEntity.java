package com.kamylo.Scrtly_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artist")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ArtistEntity {
    @Id
    private Long id;

    private String pseudonym;
    private String bannerImg;
    private String artistBio;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserEntity user;
}
