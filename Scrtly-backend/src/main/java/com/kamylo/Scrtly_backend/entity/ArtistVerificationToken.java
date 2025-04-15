package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Token must not be blank")
    @Size(max = 255, message = "Token cannot exceed 255 characters")
    @Column(nullable = false)
    private String token;

    @NotNull(message = "Expiry date must not be null")
    @Column(nullable = false)
    private Instant expiryDate;

    @NotBlank(message = "Requested artist name must not be blank")
    @Size(max = 255, message = "Requested artist name cannot exceed 255 characters")
    @Column(nullable = false)
    private String requestedArtistName;

    @OneToOne()
    @JoinColumn(nullable = false, name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}