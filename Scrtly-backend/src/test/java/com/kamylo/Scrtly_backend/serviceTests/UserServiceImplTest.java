package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.request.UserRequestDto;
import com.kamylo.Scrtly_backend.service.FileService;
import com.kamylo.Scrtly_backend.service.impl.UserServiceImpl;
import com.kamylo.Scrtly_backend.utils.UserLikeChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private FileService fileService;
    @Mock private Mapper<UserEntity, UserDto> mapper;
    @Mock private UserLikeChecker userLikeChecker;
    @Mock private MultipartFile multipartFile;

    @InjectMocks private UserServiceImpl userService;

    private UserEntity user;
    private UserEntity anotherUser;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setNickName("User1");
        user.setFullName("Test User");

        anotherUser = new UserEntity();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setNickName("User2");

        userDto = new UserDto();
        userDto.setNickName("User1");
        userDto.setFullName("Test User");
    }

    @Test
    void findUserByEmail_shouldReturnUser() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertEquals(user, userService.findUserByEmail("user@example.com"));
    }

    @Test
    void findUserByEmail_shouldThrowException_whenUserNotFound() {
        lenient().when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.findUserByEmail("unknown@example.com"));
        assertEquals("User not found", ex.getMessage());
    }


    @Test
    void findUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(user, userService.findUserById(1L));
    }

    @Test
    void findUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.findUserById(999L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void findUserByNickname_shouldReturnUserDto() {
        when(userRepository.findByNickName("User1")).thenReturn(Optional.of(user));
        when(mapper.mapTo(user)).thenReturn(userDto);
        assertEquals(userDto, userService.findUserByNickname("User1"));
    }


    @Test
    void findUserByNickname_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByNickName("unknownNick")).thenReturn(Optional.empty());
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.findUserByNickname("unknownNick"));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUserProfile_shouldReturnUserDto_withObservedStatus() {
        when(userRepository.findByNickName("User1")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));
        when(userLikeChecker.isUserFollowed(user, anotherUser.getId())).thenReturn(true);
        when(mapper.mapTo(user)).thenReturn(userDto);

        UserDto result = userService.getUserProfile("User1", "another@example.com");

        assertNotNull(result);
        assertTrue(result.isObserved());
    }

    @Test
    void followUser_shouldToggleFollowStatus() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(mapper.mapTo(anotherUser)).thenReturn(userDto);

        assertFalse(user.getFollowings().contains(anotherUser));

        UserDto followed = userService.followUser(2L, "user@example.com");
        assertTrue(user.getFollowings().contains(anotherUser));
        assertTrue(anotherUser.getFollowers().contains(user));

        UserDto unfollowed = userService.followUser(2L, "user@example.com");
        assertFalse(user.getFollowings().contains(anotherUser));
        assertFalse(anotherUser.getFollowers().contains(user));
    }

    @Test
    void followUser_shouldThrowException_whenFollowingSelf() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(CustomException.class, () -> userService.followUser(1L, "user@example.com"));
    }

    @Test
    void updateUser_shouldUpdateProfileInfo() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFullName("Updated Name");
        userRequestDto.setDescription("New Description");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userDto);

        UserDto updatedUser = userService.updateUser("user@example.com", userRequestDto, null);

        assertEquals("Updated Name", user.getFullName());
        assertEquals("New Description", user.getDescription());
    }

    @Test
    void updateUser_shouldUpdateProfilePicture_whenImageProvided() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(fileService.updateFile(multipartFile, user.getProfilePicture(), "userImages/")).thenReturn("newImagePath");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userDto);

        UserDto updatedUser = userService.updateUser("user@example.com", new UserRequestDto(), multipartFile);

        assertEquals("newImagePath", user.getProfilePicture());
    }

    @Test
    void searchUser_shouldReturnMatchingUsers() {
        Set<UserEntity> foundUsers = new HashSet<>();
        foundUsers.add(user);
        when(userRepository.searchUser("User")).thenReturn(foundUsers);
        when(mapper.mapTo(user)).thenReturn(userDto);

        Set<UserDto> result = userService.searchUser("User");

        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(dto -> "User1".equals(dto.getNickName())));
    }

    @Test
    void followUser_shouldUnfollow_whenAlreadyFollowing() {
        user.getFollowings().add(anotherUser);
        anotherUser.getFollowers().add(user);

        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(mapper.mapTo(anotherUser)).thenReturn(userDto);

        userService.followUser(2L, "user@example.com");

        assertFalse(user.getFollowings().contains(anotherUser));
        assertFalse(anotherUser.getFollowers().contains(user));
    }

    @Test
    void updateUser_shouldNotUpdateImage_whenImageIsNull() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userDto);

        UserDto updatedUser = userService.updateUser("user@example.com", new UserRequestDto(), null);

        assertNull(user.getProfilePicture());
    }

    @Test
    void updateUser_shouldNotUpdateImage_whenImageIsEmpty() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(multipartFile.isEmpty()).thenReturn(true);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userDto);

        UserDto updatedUser = userService.updateUser("user@example.com", new UserRequestDto(), multipartFile);

        assertNull(user.getProfilePicture());
    }

    @Test
    void updateUser_shouldUpdateImage_whenValidImageProvided() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileService.updateFile(multipartFile, user.getProfilePicture(), "userImages/")).thenReturn("newImagePath");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userDto);

        UserDto updatedUser = userService.updateUser("user@example.com", new UserRequestDto(), multipartFile);

        assertEquals("newImagePath", user.getProfilePicture());
    }

    @Test
    void getUserProfile_shouldThrowException_whenNickNameNotFound() {
        // Gdy użytkownik o danym nickname nie istnieje
        when(userRepository.findByNickName("nonexistentNick")).thenReturn(Optional.empty());
        // Dla reqUser stubbing nie jest istotny, bo metoda przerwie działanie już przy nickname
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserProfile("nonexistentNick", "req@example.com"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserProfile_shouldThrowException_whenReqUserNotFound() {
        // Gdy nickname istnieje, ale reqUser (użytkownik żądający) nie istnieje
        when(userRepository.findByNickName("existingNick")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("nonexistentReq@example.com")).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserProfile("existingNick", "nonexistentReq@example.com"));
        assertEquals("User not found", exception.getMessage());
    }


}
