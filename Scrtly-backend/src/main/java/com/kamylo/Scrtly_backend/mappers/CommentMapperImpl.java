package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.CommentDto;
import com.kamylo.Scrtly_backend.entity.CommentEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapperImpl implements Mapper<CommentEntity, CommentDto> {

    private final ModelMapper modelMapper;

    public CommentMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.typeMap(CommentEntity.class, CommentDto.class)
                .addMappings(mapper -> {
                    mapper.skip(CommentDto::setLikedByUser);
                });
    }

    @Override
    public CommentDto mapTo(CommentEntity commentEntity) {
        CommentDto commentDto = modelMapper.map(commentEntity, CommentDto.class);
        commentDto.setLikeCount(commentEntity.getLikes() != null ? commentEntity.getLikes().size() : 0);
        return commentDto;
    }

    @Override
    public CommentEntity mapFrom(CommentDto commentDto) {
        return modelMapper.map(commentDto, CommentEntity.class);
    }
}
