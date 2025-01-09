package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.modelmapper.ModelMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        userDto.setObserversCount(userEntity.getFollowers() != null ? userEntity.getFollowers().size() : 0);
        userDto.setObservationsCount(userEntity.getFollowings() != null ? userEntity.getFollowings().size() : 0);

        return userDto;
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}
