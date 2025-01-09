package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PostMapperImpl implements Mapper<PostEntity, PostDto> {

    private final ModelMapper modelMapper;

    public PostMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.typeMap(PostEntity.class, PostDto.class)
                .addMappings(mapper -> {
                    mapper.skip(PostDto::setLikedByUser);
                });
    }

    @Override
    public PostDto mapTo(PostEntity postEntity) {
        PostDto postDto = modelMapper.map(postEntity, PostDto.class);
        
        postDto.setLikeCount(postEntity.getLikes() != null ? postEntity.getLikes().size() : 0);
        postDto.setCommentCount(postEntity.getComments() != null ? postEntity.getComments().size() : 0);

        return postDto;
    }

    @Override
    public PostEntity mapFrom(PostDto postDto) {
        return modelMapper.map(postDto, PostEntity.class);
    }
}
