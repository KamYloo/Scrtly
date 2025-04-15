package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.entity.AlbumEntity;
import com.kamylo.Scrtly_backend.entity.SongEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AlbumMapperImpl implements Mapper<AlbumEntity, AlbumDto> {

    private ModelMapper modelMapper;

    @Override
    public AlbumDto mapTo(AlbumEntity albumEntity) {
        AlbumDto albumDto = modelMapper.map(albumEntity, AlbumDto.class);
        albumDto.setTracksCount(albumEntity.getSongs() != null ? albumEntity.getSongs().size() : 0);
        albumDto.setTotalDuration(albumEntity.getSongs() != null ? albumEntity.getSongs().stream().mapToInt(SongEntity::getDuration).sum() : 0);

        return albumDto;
    }

    @Override
    public AlbumEntity mapFrom(AlbumDto albumDto) {
        return modelMapper.map(albumDto, AlbumEntity.class);
    }
}
