package com.kamylo.Scrtly_backend.dto.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterRequestDto {
    @NotNull(message = "Firstname is required")
    private String fullName;
    @NotNull(message = "Email is required")
    @NotNull(message = "NickName is required")
    private String nickName;
    @Email(message = "Email is not formated")
    private String email;
    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password should be 8 characters long min")
    private String password;
    private String role;
    private String artistName;
}

