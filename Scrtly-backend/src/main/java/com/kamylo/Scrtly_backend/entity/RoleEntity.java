package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name must not be blank")
    @Size(max = 50, message = "Role name cannot exceed 50 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String name;
}