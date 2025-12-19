package com.kamylo.Scrtly_backend.post.mapper;

import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.post.web.dto.PostDto;
import com.kamylo.Scrtly_backend.user.mapper.UserMinimalMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {UserMinimalMapper.class}
)
public interface PostMapper {
    @Mapping(target = "likedByUser", ignore = true)
    PostDto toDto(PostEntity entity);

    PostEntity toEntity(PostDto dto);
}
