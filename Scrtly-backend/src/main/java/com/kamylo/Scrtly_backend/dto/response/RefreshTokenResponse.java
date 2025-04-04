package com.kamylo.Scrtly_backend.dto.response;

import com.kamylo.Scrtly_backend.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {
    private String refreshToken;
}
