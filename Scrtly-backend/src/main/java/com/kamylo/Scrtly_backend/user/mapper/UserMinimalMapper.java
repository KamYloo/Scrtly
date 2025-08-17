package com.kamylo.Scrtly_backend.user.mapper;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.web.dto.UserMinimalDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface UserMinimalMapper {
    UserMinimalDto toDto(UserEntity user);
}
