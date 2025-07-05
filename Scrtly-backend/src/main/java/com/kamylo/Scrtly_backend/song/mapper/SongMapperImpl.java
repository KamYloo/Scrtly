package com.kamylo.Scrtly_backend.song.mapper;

import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SongMapperImpl implements Mapper<SongEntity, SongDto> {

    private final ModelMapper modelMapper;
    private final StringRedisTemplate redis;

    @Override
    public SongDto mapTo(SongEntity songEntity) {
        SongDto dto = modelMapper.map(songEntity, SongDto.class);
        Double score = redis.opsForZSet()
                .score("song:plays:all", songEntity.getId().toString());
        dto.setPlayCount(score != null ? score.longValue() : 0L);

        return dto;
    }

    @Override
    public SongEntity mapFrom(SongDto songDto) {
        return modelMapper.map(songDto, SongEntity.class);
    }
}
