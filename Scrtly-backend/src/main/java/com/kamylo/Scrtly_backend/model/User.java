package com.kamylo.Scrtly_backend.model;
import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;



@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}

