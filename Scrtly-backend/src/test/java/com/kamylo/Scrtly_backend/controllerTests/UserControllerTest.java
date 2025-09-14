package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.artist.web.dto.request.ArtistVerificationRequest;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.user.web.controller.UserController;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.user.web.dto.request.UserRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Mock
    private Principal principal;

    private UserDto sampleUser(Long id, String nick) {
        UserDto u = new UserDto();
        u.setId(id);
        u.setNickName(nick);
        u.setEmail(nick + "@ex.com");
        u.setFullName("Full " + nick);
        return u;
    }

    @Test
    void getProfile_withPrincipal_returnsUserDto() {
        when(principal.getName()).thenReturn("caller");
        UserDto expected = sampleUser(1L, "nick");
        when(userService.getUserProfile(eq("nick"), eq("caller"))).thenReturn(expected);

        var resp = controller.getProfile("nick", principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(expected, resp.getBody());
        verify(userService).getUserProfile("nick", "caller");
    }

    @Test
    void getProfile_withoutPrincipal_passesNullUsername() {
        UserDto expected = sampleUser(2L, "anon");
        when(userService.getUserProfile(eq("anon"), isNull())).thenReturn(expected);

        var resp = controller.getProfile("anon", null);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(expected, resp.getBody());
        verify(userService).getUserProfile("anon", null);
    }

    @Test
    void searchUser_returnsSetOfUsers() {
        var result = Set.of(sampleUser(3L, "a"));
        when(userService.searchUser("query")).thenReturn(result);

        var resp = controller.searchUser("query");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(result, resp.getBody());
        verify(userService).searchUser("query");
    }

    @Test
    void updateUser_callsService_andReturnsAccepted() {
        when(principal.getName()).thenReturn("me");
        UserRequestDto req = UserRequestDto.builder().fullName("New Name").description("desc").build();
        MultipartFile file = mock(MultipartFile.class);
        UserDto updated = sampleUser(4L, "me");
        when(userService.updateUser("me", req, file)).thenReturn(updated);

        var resp = controller.updateUser(req, file, principal);

        assertEquals(HttpStatus.ACCEPTED, resp.getStatusCode());
        assertEquals(updated, resp.getBody());
        verify(userService).updateUser("me", req, file);
    }

    @Test
    void followUser_callsService_andReturnsOk() {
        when(principal.getName()).thenReturn("follower");
        Long userId = 10L;
        UserDto dto = sampleUser(userId, "target");
        when(userService.followUser(userId, "follower")).thenReturn(dto);

        var resp = controller.followUser(userId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(userService).followUser(userId, "follower");
    }

    @Test
    void verifyAsArtist_callsService_andReturnsOk() {
        when(principal.getName()).thenReturn("requester");
        ArtistVerificationRequest req = mock(ArtistVerificationRequest.class);

        var resp = controller.verifyAsArtist(req, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(userService).requestArtistVerification("requester", req);
    }
}