package com.kamylo.Scrtly_backend.auth.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestPasswordRequest {
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be 8 characters long min")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, message = "Password confirmation should be 8 characters long min")
    private String passwordConfirmation;
}