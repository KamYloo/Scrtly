package com.kamylo.Scrtly_backend.album.mapper;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.mapper.ArtistMapper;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import org.mapstruct.*;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {ArtistMapper.class}
)
public interface AlbumMapper {

    @Mapping(source = "coverImage", target = "albumImage")
    @Mapping(target = "tracksCount", ignore = true)
    @Mapping(target = "totalDuration", ignore = true)
    AlbumDto toDto(AlbumEntity entity);

    @AfterMapping
    default void enrich(AlbumEntity entity, @MappingTarget AlbumDto dto) {
        List<SongEntity> songs = entity.getSongs();
        int count = songs != null ? songs.size() : 0;
        int duration = songs != null ? songs.stream().mapToInt(SongEntity::getDuration).sum() : 0;
        dto.setTracksCount(count);
        dto.setTotalDuration(duration);
    }

    @InheritInverseConfiguration
    @Mapping(target = "songs", ignore = true)
    @Mapping(target = "artist", ignore = true)
    @Mapping(target = "coverImage", source = "albumImage")
    AlbumEntity toEntity(AlbumDto dto);
}
