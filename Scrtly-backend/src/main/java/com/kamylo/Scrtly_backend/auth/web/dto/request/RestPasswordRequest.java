package com.kamylo.Scrtly_backend.auth.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestPasswordRequest {
    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password should be 6 characters long min")
    private String password;

    @NotNull(message = "Password confirmation is required")
    @Size(min = 6, message = "Password confirmation should be 6 characters long min")
    private String passwordConfirmation;
}