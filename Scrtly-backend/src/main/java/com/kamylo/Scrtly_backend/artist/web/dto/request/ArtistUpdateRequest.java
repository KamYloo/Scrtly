package com.kamylo.Scrtly_backend.artist.web.dto.request;

import com.kamylo.Scrtly_backend.common.config.FileConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistUpdateRequest {
    @FileConstraint(allowed = {"image/png","image/jpeg"}, maxSizeKb = 10240, message = "{image.tooLarge}")
    private MultipartFile bannerImg;

    @Size(max = 1000, message = "{artist.bio.size}")
    private String artistBio;
}
