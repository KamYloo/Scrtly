package com.kamylo.Scrtly_backend.artist.mapper;

import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class ArtistMapperImpl implements Mapper<ArtistEntity, ArtistDto> {

    private ModelMapper modelMapper;

    @Override
    public ArtistDto mapTo(ArtistEntity artistEntity) {
        ArtistDto artistDto = modelMapper.map(artistEntity, ArtistDto.class);
        artistDto.setTotalFans(
                Optional.ofNullable(artistEntity.getUser())
                        .map(UserEntity::getFollowers)
                        .map(Set::size)
                        .orElse(0)
        );

        return artistDto;
    }

    @Override
    public ArtistEntity mapFrom(ArtistDto artistDto) {
        return modelMapper.map(artistDto, ArtistEntity.class);
    }
}

