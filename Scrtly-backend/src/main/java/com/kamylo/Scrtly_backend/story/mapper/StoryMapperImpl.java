package com.kamylo.Scrtly_backend.story.mapper;

import com.kamylo.Scrtly_backend.story.web.dto.StoryDto;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.story.domain.StoryEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StoryMapperImpl implements Mapper<StoryEntity, StoryDto> {

    private ModelMapper modelMapper;

    @Override
    public StoryDto mapTo(StoryEntity storyEntity) {
        return modelMapper.map(storyEntity, StoryDto.class);
    }

    @Override
    public StoryEntity mapFrom(StoryDto storyDto) {
        return modelMapper.map(storyDto, StoryEntity.class);
    }
}
