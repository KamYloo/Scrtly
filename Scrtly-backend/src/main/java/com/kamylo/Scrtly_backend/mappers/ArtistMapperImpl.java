package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArtistMapperImpl implements Mapper<ArtistEntity, ArtistDto> {

    private ModelMapper modelMapper;

    @Override
    public ArtistDto mapTo(ArtistEntity artistEntity) {
        ArtistDto artistDto = modelMapper.map(artistEntity, ArtistDto.class);
        artistDto.setTotalFans(artistEntity.getFollowers() != null ? artistEntity.getFollowers().size() : 0);
        return artistDto;
    }

    @Override
    public ArtistEntity mapFrom(ArtistDto artistDto) {
        return modelMapper.map(artistDto, ArtistEntity.class);
    }
}

