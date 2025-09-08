package com.kamylo.Scrtly_backend.story.web.dto.request;


import com.kamylo.Scrtly_backend.common.config.FileConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryRequest {

    @NotNull(message = "{story.file.required}")
    @FileConstraint(
            allowed = {"image/jpeg", "image/png"},
            maxSizeKb = 10240,
            message = "{story.file.invalid}"
    )
    private MultipartFile file;
}
