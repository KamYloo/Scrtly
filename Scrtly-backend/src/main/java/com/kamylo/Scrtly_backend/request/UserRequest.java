package com.kamylo.Scrtly_backend.request;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String password;
    private String fullName;
    private String role;
    private String artistName;
}
