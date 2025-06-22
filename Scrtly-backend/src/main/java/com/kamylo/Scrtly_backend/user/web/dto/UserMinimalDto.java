package com.kamylo.Scrtly_backend.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMinimalDto {
    private Long id;
    private String fullName;
    private String nickName;
    private String profilePicture;
}
