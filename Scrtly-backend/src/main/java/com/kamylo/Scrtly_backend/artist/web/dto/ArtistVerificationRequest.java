package com.kamylo.Scrtly_backend.artist.web.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ArtistVerificationRequest {
    @NotNull(message = "ArtistName is required")
    private String requestedArtistName;
}
