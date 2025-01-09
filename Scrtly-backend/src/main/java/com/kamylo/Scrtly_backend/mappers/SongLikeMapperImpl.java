package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.SongLikeDto;
import com.kamylo.Scrtly_backend.entity.SongLikeEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SongLikeMapperImpl implements Mapper<SongLikeEntity, SongLikeDto> {

    private ModelMapper modelMapper;

    @Override
    public SongLikeDto mapTo(SongLikeEntity songLikeEntity) {
        return modelMapper.map(songLikeEntity, SongLikeDto.class);
    }

    @Override
    public SongLikeEntity mapFrom(SongLikeDto songLikeDto) {
        return modelMapper.map(songLikeDto, SongLikeEntity.class);
    }
}