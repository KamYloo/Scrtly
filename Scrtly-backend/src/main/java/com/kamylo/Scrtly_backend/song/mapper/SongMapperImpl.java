package com.kamylo.Scrtly_backend.song.mapper;

import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SongMapperImpl implements Mapper<SongEntity, SongDto> {

    private ModelMapper modelMapper;

    @Override
    public SongDto mapTo(SongEntity songEntity) {
        return modelMapper.map(songEntity, SongDto.class);
    }

    @Override
    public SongEntity mapFrom(SongDto songDto) {
        return modelMapper.map(songDto, SongEntity.class);
    }
}
