package com.kamylo.Scrtly_backend.artist.domain;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "artist", indexes = {
        @Index(name = "idx_artist_pseudonym", columnList = "pseudonym", unique = true)
})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ArtistEntity {
    @Id
    private Long id;

    @NotBlank(message = "Pseudonym must not be blank")
    @Size(max = 100, message = "Pseudonym cannot exceed 100 characters")
    private String pseudonym;

    @Size(max = 255, message = "Banner image URL must be at most 255 characters")
    private String bannerImg;

    @Size(max = 1000, message = "Artist bio cannot exceed 1000 characters")
    private String artistBio;


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private UserEntity user;
}
