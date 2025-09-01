package com.kamylo.Scrtly_backend.post.web.dto.request;

import com.kamylo.Scrtly_backend.common.config.FileConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PostUpdateRequest {
    @Size(max = 1000, message = "{post.description.size}")
    private String description;

    @FileConstraint(allowed = {"image/png","image/jpeg"}, maxSizeKb = 10240, message = "{image.tooLarge}")
    private MultipartFile file;
}