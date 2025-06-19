package com.kamylo.Scrtly_backend.auth.domain;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Token must not be blank")
    @Size(max = 255, message = "Token cannot exceed 255 characters")
    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne()
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private UserEntity user;

    @NotNull(message = "Expiry date must not be null")
    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
