package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.entity.LikeEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PostLikeMapperImpl implements Mapper<LikeEntity, PostStatsDto> {

    private final ModelMapper modelMapper;

    public PostLikeMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.typeMap(LikeEntity.class, PostStatsDto.class)
                .addMappings(mapper -> {
                    mapper.skip(PostStatsDto::setLikedByUser);
                });
    }

    @Override
    public PostStatsDto mapTo(LikeEntity likeEntity) {
        PostStatsDto postStatsDto = modelMapper.map(likeEntity, PostStatsDto.class);
        postStatsDto.setLikeCount(likeEntity.getPost().getLikes() != null ? likeEntity.getPost().getLikes().size() : 0);
        postStatsDto.setCommentCount(likeEntity.getPost().getComments() != null ? likeEntity.getPost().getComments().size() : 0);

        return postStatsDto;
    }

    @Override
    public LikeEntity mapFrom(PostStatsDto postStatsDto) {
        return modelMapper.map(postStatsDto, LikeEntity.class);
    }
}
