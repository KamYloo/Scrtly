package com.kamylo.Scrtly_backend.dto.Minimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostMinimalDto {
    private Long id;
    private String image;
    private String description;
    private UserMinimalDto user;
    private LocalDateTime creationDate;
}
