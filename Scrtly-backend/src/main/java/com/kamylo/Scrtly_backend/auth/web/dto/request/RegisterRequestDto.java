package com.kamylo.Scrtly_backend.auth.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRequestDto {
    @NotBlank(message = "fullName is required")
    private String fullName;
    @NotBlank(message = "NickName is required")
    private String nickName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not formated")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be 8 characters long min")
    private String password;
}

