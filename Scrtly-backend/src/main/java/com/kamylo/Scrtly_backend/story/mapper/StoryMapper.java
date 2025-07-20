package com.kamylo.Scrtly_backend.story.mapper;

import com.kamylo.Scrtly_backend.story.domain.StoryEntity;
import com.kamylo.Scrtly_backend.story.web.dto.StoryDto;
import com.kamylo.Scrtly_backend.user.mapper.UserMinimalMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {UserMinimalMapper.class}
)
public interface StoryMapper {

    StoryDto toDto(StoryEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "user", ignore = true)
    StoryEntity toEntity(StoryDto dto);
}
