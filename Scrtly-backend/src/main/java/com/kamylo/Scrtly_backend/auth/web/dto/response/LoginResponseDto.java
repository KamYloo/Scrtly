package com.kamylo.Scrtly_backend.auth.web.dto.response;

import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private UserDto user;
}
