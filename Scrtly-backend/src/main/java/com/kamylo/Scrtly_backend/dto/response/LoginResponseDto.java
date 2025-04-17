package com.kamylo.Scrtly_backend.dto.response;

import com.kamylo.Scrtly_backend.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private UserDto user;
}
