package com.kamylo.Scrtly_backend.playList.mapper;

import com.kamylo.Scrtly_backend.playList.domain.PlayListEntity;
import com.kamylo.Scrtly_backend.playList.web.dto.PlayListDto;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {UserMapper.class}
)
public interface PlayListMapper {

    @Mapping(target = "tracksCount", ignore = true)
    @Mapping(target = "totalDuration", ignore = true)
    PlayListDto toDto(PlayListEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "songs", ignore = true)
    @Mapping(target = "user", ignore = true)
    PlayListEntity toEntity(PlayListDto dto);

    @AfterMapping
    default void enrich(PlayListEntity entity, @MappingTarget PlayListDto dto) {
        dto.setTracksCount(entity.getSongs() != null ? entity.getSongs().size() : 0);
        dto.setTotalDuration(entity.getSongs() != null ? entity.getSongs().stream().mapToInt(SongEntity::getDuration).sum() : 0);
    }
}
