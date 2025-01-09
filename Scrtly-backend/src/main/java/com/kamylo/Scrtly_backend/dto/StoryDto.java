package com.kamylo.Scrtly_backend.dto;

import com.kamylo.Scrtly_backend.dto.Minimal.UserMinimalDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoryDto {
    private Long id;
    private String image;
    private LocalDateTime timestamp;
    private UserMinimalDto user;
}
