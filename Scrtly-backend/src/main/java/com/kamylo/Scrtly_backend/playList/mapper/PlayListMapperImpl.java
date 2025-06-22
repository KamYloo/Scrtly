package com.kamylo.Scrtly_backend.playList.mapper;

import com.kamylo.Scrtly_backend.playList.web.dto.PlayListDto;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.playList.domain.PlayListEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayListMapperImpl implements Mapper<PlayListEntity, PlayListDto> {

    private ModelMapper modelMapper;

    @Override
    public PlayListDto mapTo(PlayListEntity playListEntity) {
        PlayListDto playListDto = modelMapper.map(playListEntity, PlayListDto.class);
        playListDto.setTracksCount(playListEntity.getSongs() != null ? playListEntity.getSongs().size() : 0);
        playListDto.setTotalDuration(playListEntity.getSongs() != null ? playListEntity.getSongs().stream().mapToInt(SongEntity::getDuration).sum() : 0);
        return playListDto;
    }

    @Override
    public PlayListEntity mapFrom(PlayListDto playListDto) {
        return modelMapper.map(playListDto, PlayListEntity.class);
    }
}