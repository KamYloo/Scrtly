package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.config.JwtProvider;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileServiceImplementation fileService;

    @InjectMocks
    private UserServiceImplementation userService;

    private MockedStatic<JwtProvider> jwtProviderMockedStatic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtProviderMockedStatic = mockStatic(JwtProvider.class);
    }

    @AfterEach
    void tearDown() {
        jwtProviderMockedStatic.close();
    }

    @Test
    void testFindUserProfileByJwtSuccess() throws UserException {
        String jwt = "validJwtToken";
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        jwtProviderMockedStatic.when(() -> JwtProvider.getEmailFromJwtToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.findUserProfileByJwt(jwt);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testFindUserProfileByJwtInvalidJwt() {
        String jwt = "invalidJwtToken";

        jwtProviderMockedStatic.when(() -> JwtProvider.getEmailFromJwtToken(jwt)).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> userService.findUserProfileByJwt(jwt));
    }

    @Test
    void testFindUserProfileByJwtUserNotFound() {
        String jwt = "validJwtToken";
        String email = "test@example.com";

        jwtProviderMockedStatic.when(() -> JwtProvider.getEmailFromJwtToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UserException.class, () -> userService.findUserProfileByJwt(jwt));
    }

    @Test
    void testFindUserByIdSuccess() throws UserException {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void testFindUserByIdNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.findUserById(userId));
    }

    @Test
    void testFollowUserSuccess() throws UserException {
        Long userId = 1L;
        User user = new User();
        User followToUser = new User();
        followToUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(followToUser));

        User result = userService.followUser(userId, user);

        assertNotNull(result);
        assertTrue(user.getFollowings().contains(followToUser));
        assertTrue(followToUser.getFollowers().contains(user));

        verify(userRepository, times(1)).save(followToUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFollowUserUnfollow() throws UserException {
        Long userId = 1L;
        User user = new User();
        User followToUser = new User();
        followToUser.setId(userId);

        user.getFollowings().add(followToUser);
        followToUser.getFollowers().add(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(followToUser));

        User result = userService.followUser(userId, user);

        assertNotNull(result);
        assertFalse(user.getFollowings().contains(followToUser));
        assertFalse(followToUser.getFollowers().contains(user));

        verify(userRepository, times(1)).save(followToUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserSuccess() throws UserException {
        Long userId = 1L;
        String fullName = "New Full Name";
        String description = "New Description";
        MultipartFile userImage = mock(MultipartFile.class);
        User user = new User();
        user.setId(userId);
        user.setProfilePicture("oldImage.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileService.updateFile(userImage, "/oldImage.jpg", "/uploads/userImages")).thenReturn("newImage.jpg");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(userId, fullName, description, userImage);

        assertNotNull(result);
        assertEquals(fullName, result.getFullName());
        assertEquals(description, result.getDescription());
        assertEquals("uploads/userImages/newImage.jpg", result.getProfilePicture());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSearchUserSuccess() {
        String query = "John";
        User user = new User();
        user.setFullName("John Doe");

        when(userRepository.searchUser(query)).thenReturn(Set.of(user));

        Set<User> result = userService.searchUser(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(user));
    }

    @Test
    void testSearchUserEmptyResult() {
        String query = "NonExistentUser";

        when(userRepository.searchUser(query)).thenReturn(Collections.emptySet());

        Set<User> result = userService.searchUser(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
