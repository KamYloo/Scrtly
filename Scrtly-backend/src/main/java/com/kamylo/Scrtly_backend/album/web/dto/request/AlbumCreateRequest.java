package com.kamylo.Scrtly_backend.album.web.dto.request;

import com.kamylo.Scrtly_backend.common.config.FileConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumCreateRequest {
    @NotBlank(message = "{album.title.notblank}")
    @Size(max = 30, message = "{album.title.size}")
    private String title;

    @NotNull(message = "{album.image.required}")
    @FileConstraint(allowed = {"image/png","image/jpeg"}, maxSizeKb = 10240, message = "{image.tooLarge}")
    private MultipartFile file;
}
