package com.kamylo.Scrtly_backend.song.web.dto;

import com.kamylo.Scrtly_backend.common.config.FileConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongRequest {
    @NotBlank(message = "{song.title.notblank}")
    @Size(max = 255, message = "{song.title.size}")
    private String title;

    @Positive(message = "{song.albumId.positive}")
    private Integer albumId;

    @NotNull(message = "{song.image.required}")
    @FileConstraint(allowed = {"image/png","image/jpeg"}, maxSizeKb = 10240, message = "{song.image.invalid}")
    private MultipartFile imageSong;

    @NotNull(message = "{song.audio.required}")
    @FileConstraint(allowed = {"audio/mpeg","audio/mp3","audio/wav"}, maxSizeKb = 10240, message = "{song.audio.invalid}")
    private MultipartFile audioFile;
}
