package com.kamylo.Scrtly_backend.auth.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRequestDto {
    @NotNull(message = "fullName is required")
    private String fullName;
    @NotNull(message = "NickName is required")
    private String nickName;
    @NotNull(message = "Email is required")
    @Email(message = "Email is not formated")
    private String email;
    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password should be 6 characters long min")
    private String password;
}

