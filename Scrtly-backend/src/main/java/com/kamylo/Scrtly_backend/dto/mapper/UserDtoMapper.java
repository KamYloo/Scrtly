package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.model.User;
import java.util.HashSet;
import java.util.Set;

public class UserDtoMapper {

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setDescription(user.getDescription());
        userDto.setProfilePicture(user.getProfilePicture());
        userDto.setFollowers(toUserDtos(user.getFollowers()));
        userDto.setFollowing(toUserDtos(user.getFollowings()));

        return userDto;
    }

    public static Set<UserDto> toUserDtos(Set<User> followers) {
        Set<UserDto> userDtos = new HashSet<>();
        for (User user : followers) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setProfilePicture(user.getProfilePicture());
            userDto.setFullName(user.getFullName());
            userDtos.add(userDto);
        }
        return userDtos;
    }
}
