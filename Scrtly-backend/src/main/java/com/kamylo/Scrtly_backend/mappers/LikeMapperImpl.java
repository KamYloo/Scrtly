package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.LikeDto;
import com.kamylo.Scrtly_backend.entity.LikeEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LikeMapperImpl implements Mapper<LikeEntity, LikeDto> {

    private ModelMapper modelMapper;

    @Override
    public LikeDto mapTo(LikeEntity likeEntity) {
        return modelMapper.map(likeEntity, LikeDto.class);
    }

    @Override
    public LikeEntity mapFrom(LikeDto likeDto) {
        return modelMapper.map(likeDto, LikeEntity.class);
    }
}
