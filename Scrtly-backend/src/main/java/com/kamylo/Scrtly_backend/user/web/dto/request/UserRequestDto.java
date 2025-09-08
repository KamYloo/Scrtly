package com.kamylo.Scrtly_backend.user.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotBlank(message = "{user.fullName.notblank}")
    @Size(min = 2, max = 100, message = "{user.fullName.size}")
    private String fullName;

    @Size(max = 500, message = "{user.description.size}")
    private String description;
}
