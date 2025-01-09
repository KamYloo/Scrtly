package com.kamylo.Scrtly_backend.service.impl;
import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.request.UserRequestDto;
import com.kamylo.Scrtly_backend.service.FileService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.utils.UserLikeChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileService fileService;
    private final Mapper<UserEntity, UserDto> mapper;
    private final UserLikeChecker userLikeChecker;

    @Override
    public UserEntity findUserByEmail(String email) {
        return  userRepository.findByEmail(email).orElseThrow(
                ()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                ()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDto findUserByNickname(String nickname) {
        return  mapper.mapTo(userRepository.findByNickName(nickname).orElseThrow(
                ()->new UsernameNotFoundException("User not found")));
    }

    @Override
    public UserDto getUserProfile(String nickname, String reqUsername) {
        UserEntity user = userRepository.findByNickName(nickname).orElseThrow(
                ()->new UsernameNotFoundException("User not found"));
        UserEntity reqUser = userRepository.findByEmail(reqUsername).orElseThrow(
                ()->new UsernameNotFoundException("User not found"));
        UserDto userDto = mapper.mapTo(user);
        userDto.setObserved(userLikeChecker.isUserFollowed(user,reqUser.getId()));
        return userDto;
    }

    @Override
    @Transactional
    public UserDto followUser(Long userId, String username) {
        UserEntity followToUser = findUserById(userId);
        UserEntity reqUser = findUserByEmail(username);

        if (reqUser.equals(followToUser)) {
            throw new CustomException(BusinessErrorCodes.FOLLOW_ERROR);
        }

        if (reqUser.getFollowings().contains(followToUser) && followToUser.getFollowers().contains(reqUser)) {
            reqUser.getFollowings().remove(followToUser);
            followToUser.getFollowers().remove(reqUser);
        }
        else {
            reqUser.getFollowings().add(followToUser);
            followToUser.getFollowers().add(reqUser);
        }

        userRepository.save(followToUser);
        userRepository.save(reqUser);
        UserDto userDto = mapper.mapTo(followToUser);
        userDto.setObserved(userLikeChecker.isUserFollowed(followToUser,reqUser.getId()));
        return userDto;
    }

    @Override
    @Transactional
    public UserDto updateUser(String username, UserRequestDto userRequestDto, MultipartFile userImage) {
        UserEntity user = findUserByEmail(username);

        if (userRequestDto.getFullName() != null) {
            user.setFullName(userRequestDto.getFullName());
        }
        if (userRequestDto.getDescription() != null) {
            user.setDescription(userRequestDto.getDescription());
        }

        if (userImage != null && !userImage.isEmpty()) {
            String imagePath = fileService.updateFile(userImage, user.getProfilePicture(), "userImages/");
            user.setProfilePicture(imagePath);
        }

        return mapper.mapTo(userRepository.save(user));
    }

    @Override
    public Set<UserDto> searchUser(String query) {
        return userRepository.searchUser(query).stream()
                .map(mapper::mapTo)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
