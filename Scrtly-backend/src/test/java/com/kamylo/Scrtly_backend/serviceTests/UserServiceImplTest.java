package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistVerificationRequest;
import com.kamylo.Scrtly_backend.user.web.dto.request.UserRequestDto;
import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.user.domain.RoleEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.artist.service.ArtistVerificationTokenService;
import com.kamylo.Scrtly_backend.email.service.EmailService;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.user.service.impl.UserServiceImpl;
import com.kamylo.Scrtly_backend.common.utils.UserLikeChecker;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private FileService fileService;
    @Mock private Mapper<UserEntity, UserDto> mapper;
    @Mock private UserLikeChecker userLikeChecker;
    @Mock private MultipartFile multipartFile;
    @Mock private ArtistVerificationTokenService artistVerificationTokenService;
    @Mock private EmailService emailService;

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
        user.setRoles(new HashSet<>());

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
        when(userRepository.findByNickName("nonexistentNick")).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserProfile("nonexistentNick", "req@example.com"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserProfile_shouldThrowException_whenReqUserNotFound() {
        when(userRepository.findByNickName("existingNick")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("nonexistentReq@example.com")).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserProfile("existingNick", "nonexistentReq@example.com"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void requestArtistVerification_shouldSendEmailToAdmins() throws MessagingException {
        ArtistVerificationRequest request = new ArtistVerificationRequest();
        request.setRequestedArtistName("requestedArtist");

        ArtistVerificationToken tokenInstance = new ArtistVerificationToken();
        tokenInstance.setToken("testToken");
        tokenInstance.setRequestedArtistName("requestedArtist");

        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(null);
        when(artistVerificationTokenService.createArtistVerificationToken(user, "requestedArtist")).thenReturn(tokenInstance);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName("ADMIN");
        Set<RoleEntity> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        UserEntity admin = new UserEntity();
        admin.setId(100L);
        admin.setEmail("admin@example.com");
        admin.setFullName("Admin User");
        admin.setRoles(adminRoles);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(admin));

        userService.requestArtistVerification("user@example.com", request);

        verify(emailService, times(1)).sendEmail(eq("admin@example.com"),
                eq("Admin User"),
                eq(EmailTemplateName.ARTIST_VERIFICATION),
                anyString(),
                eq("Artist Verification"),
                eq("requestedArtist"),
                eq(user));
    }

    @Test
    void requestArtistVerification_shouldThrowException_whenUserIsAlreadyArtist() {
        RoleEntity artistRole = new RoleEntity();
        artistRole.setName("ARTIST");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(artistRole);
        user.setRoles(roles);

        ArtistVerificationRequest request = new ArtistVerificationRequest();
        request.setRequestedArtistName("requestedArtist");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertThrows(CustomException.class, () ->
                userService.requestArtistVerification("user@example.com", request));
    }
}
