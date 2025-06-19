package com.kamylo.Scrtly_backend.album.mapper;

import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
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
