package com.kamylo.Scrtly_backend.song.mapper;

import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.album.mapper.AlbumMapper;
import com.kamylo.Scrtly_backend.artist.mapper.ArtistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = { ArtistMapper.class, AlbumMapper.class }
)
public abstract class SongMapper {

    protected StringRedisTemplate redis;

    @Autowired
    public void setRedis(StringRedisTemplate redis) {
        this.redis = redis;
    }


    @Mapping(target = "playCount", ignore = true)
    public abstract SongDto toDto(SongEntity entity);

    @AfterMapping
    protected void enrichWithPlayCount(SongEntity entity, @MappingTarget SongDto dto) {
        Double score = redis.opsForZSet()
                .score("song:plays:all", entity.getId().toString());
        dto.setPlayCount(score != null ? score.longValue() : 0L);
    }

    @InheritInverseConfiguration
    @Mapping(target = "playlists", ignore = true)
    @Mapping(target = "album", ignore = true)
    @Mapping(target = "artist", ignore = true)
    public abstract SongEntity toEntity(SongDto dto);
}
