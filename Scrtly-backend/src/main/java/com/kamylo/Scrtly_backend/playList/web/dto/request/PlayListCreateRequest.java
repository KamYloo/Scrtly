package com.kamylo.Scrtly_backend.playList.web.dto.request;

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
public class PlayListCreateRequest {
    @NotBlank(message = "{playlist.title.notblank}")
    @Size(max = 30, message = "{playlist.title.size}")
    private String title;

    @NotNull(message = "{playlist.cover.required}")
    @FileConstraint(allowed = {"image/png","image/jpeg"}, maxSizeKb = 10240, message = "{image.tooLarge}")
    private MultipartFile file;
}
