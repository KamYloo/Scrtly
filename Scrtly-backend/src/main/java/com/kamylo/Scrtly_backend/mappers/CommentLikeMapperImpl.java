package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.CommentStatsDto;
import com.kamylo.Scrtly_backend.entity.LikeEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentLikeMapperImpl implements Mapper<LikeEntity, CommentStatsDto>{
    private final ModelMapper modelMapper;

    public CommentLikeMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.typeMap(LikeEntity.class, CommentStatsDto.class)
                .addMappings(mapper -> {
                    mapper.skip(CommentStatsDto::setLikedByUser);
                });
    }

    @Override
    public CommentStatsDto mapTo(LikeEntity likeEntity) {
        CommentStatsDto commentStatsDto = modelMapper.map(likeEntity, CommentStatsDto.class);
        commentStatsDto.setLikeCount(likeEntity.getComment().getLikes() != null ? likeEntity.getComment().getLikes().size() : 0);
        return commentStatsDto;
    }

    @Override
    public LikeEntity mapFrom(CommentStatsDto commentStatsDto) {
        return modelMapper.map(commentStatsDto, LikeEntity.class);
    }
}
