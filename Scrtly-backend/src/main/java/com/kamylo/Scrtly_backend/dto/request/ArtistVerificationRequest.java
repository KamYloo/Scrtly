package com.kamylo.Scrtly_backend.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ArtistVerificationRequest {
    @NotNull(message = "ArtistName is required")
    private String requestedArtistName;
}
