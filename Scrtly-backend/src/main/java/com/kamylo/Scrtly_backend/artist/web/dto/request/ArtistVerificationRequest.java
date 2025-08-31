package com.kamylo.Scrtly_backend.artist.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArtistVerificationRequest {
    @NotNull(message = "ArtistName is required")
    private String requestedArtistName;
}
