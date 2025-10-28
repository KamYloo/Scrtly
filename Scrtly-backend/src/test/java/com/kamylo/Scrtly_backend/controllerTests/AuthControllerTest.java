package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.auth.domain.RefreshTokenEntity;
import com.kamylo.Scrtly_backend.auth.service.AuthService;
import com.kamylo.Scrtly_backend.auth.service.CookieService;
import com.kamylo.Scrtly_backend.auth.service.JwtService;
import com.kamylo.Scrtly_backend.auth.service.RefreshTokenService;
import com.kamylo.Scrtly_backend.auth.web.controller.AuthController;
import com.kamylo.Scrtly_backend.auth.web.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RestPasswordRequest;
import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import com.kamylo.Scrtly_backend.auth.web.dto.response.LoginResponseDto;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthService authService;
    @Mock private UserService userService;
    @Mock private CookieService cookieService;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private UserMapper userMapper;

    @InjectMocks private AuthController controller;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @BeforeEach
    void setUp() throws Exception {
        Field f = AuthController.class.getDeclaredField("redirectUrl");
        f.setAccessible(true);
        f.set(controller, "http://frontend/activated");
    }

    private UserDto sampleUserDto(String email) {
        UserDto u = new UserDto();
        u.setEmail(email);
        return u;
    }

    private UserEntity sampleUserEntity(String email) {
        UserEntity e = new UserEntity();
        e.setEmail(email);
        return e;
    }

    @Test
    void register_callsService_andReturnsCreated() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto();
        req.setEmail("a@b.com");
        req.setFullName("Full");
        req.setNickName("nick");
        req.setPassword("password123");

        UserDto created = sampleUserDto("a@b.com");
        when(authService.createUser(req)).thenReturn(created);

        var resp = controller.register(req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(created, resp.getBody());
        verify(authService).createUser(req);
    }

    @Test
    void login_addsCookies_andReturnsUser() throws Exception {
        LoginRequestDto lr = new LoginRequestDto();
        lr.setEmail("x@y.com");
        lr.setPassword("pw");

        when(authService.verify(lr)).thenReturn(Map.of("jwt", "jwt-token", "refresh", "refresh-token"));
        when(cookieService.getNewCookie(eq("jwt"), anyString(), anyInt())).thenReturn(new Cookie("jwt", "jwt-token"));
        when(cookieService.getNewCookie(eq("refresh"), anyString(), anyInt())).thenReturn(new Cookie("refresh", "refresh-token"));

        UserEntity userEntity = sampleUserEntity(lr.getEmail());
        when(userService.findUserByEmail(lr.getEmail())).thenReturn(userEntity);

        UserDto userDto = sampleUserDto(lr.getEmail());
        when(userMapper.toDto(userEntity)).thenReturn(userDto);
        when(userService.isPremium(lr.getEmail())).thenReturn(true);

        var resp = controller.login(lr, response);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody() instanceof LoginResponseDto);
        LoginResponseDto body = (LoginResponseDto) resp.getBody();
        assertEquals(userDto, body.getUser());
        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(authService).verify(lr);
        verify(userService).findUserByEmail(lr.getEmail());
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void refresh_success_refreshesTokens_andReturnsOk() {
        when(cookieService.getCookieValue(request, "refresh")).thenReturn("raw-refresh");
        RefreshTokenEntity tokenEntity = new RefreshTokenEntity();
        UserEntity user = sampleUserEntity("p@q.com");
        tokenEntity.setUser(user);
        when(refreshTokenService.findByToken("raw-refresh")).thenReturn(tokenEntity);
        when(jwtService.generateToken(user.getEmail())).thenReturn("new-jwt");
        RefreshTokenResponse r = new RefreshTokenResponse("new-refresh");
        when(refreshTokenService.createRefreshToken(user.getEmail())).thenReturn(r);
        when(cookieService.getNewCookie(eq("jwt"), anyString(), anyInt())).thenReturn(new Cookie("jwt", "new-jwt"));
        when(cookieService.getNewCookie(eq("refresh"), anyString(), anyInt())).thenReturn(new Cookie("refresh", "new-refresh"));

        var resp = controller.refresh(request, response);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Tokens refreshed successfully", resp.getBody());
        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(refreshTokenService).findByToken("raw-refresh");
        verify(refreshTokenService).verifyExpiration(tokenEntity);
    }

    @Test
    void refresh_expired_deletesCookies_andReturnsBadRequest() {
        when(cookieService.getCookieValue(request, "refresh")).thenReturn("raw-refresh");
        when(refreshTokenService.findByToken("raw-refresh")).thenThrow(new CustomException(BusinessErrorCodes.TOKEN_EXPIRED));

        when(cookieService.deleteCookie("jwt")).thenReturn(new Cookie("jwt", ""));
        when(cookieService.deleteCookie("refresh")).thenReturn(new Cookie("refresh", ""));

        var resp = controller.refresh(request, response);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Refresh token expired", resp.getBody());
        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(refreshTokenService).findByToken("raw-refresh");
    }

    @Test
    void logout_deletesCookies_andReturnsOk() {
        when(cookieService.getCookieValue(request, "jwt")).thenReturn("jwt-token");
        when(jwtService.extractUserName("jwt-token")).thenReturn("user@mail");
        when(cookieService.deleteCookie("jwt")).thenReturn(new Cookie("jwt", ""));
        when(cookieService.deleteCookie("refresh")).thenReturn(new Cookie("refresh", ""));

        var resp = controller.logout(request, response);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Logged out successfully", resp.getBody());
        verify(refreshTokenService).deleteByUserEmail("user@mail");
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    void checkAuth_whenUnauthenticated_returnsUnauthorized() {
        var resp = controller.checkAuth(null);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void checkAuth_whenAuthenticated_returnsUserDto() {
        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("me@mail");
        UserEntity ue = sampleUserEntity("me@mail");
        when(userService.findUserByEmail("me@mail")).thenReturn(ue);
        UserDto dto = sampleUserDto("me@mail");
        when(userMapper.toDto(ue)).thenReturn(dto);
        when(userService.isPremium("me@mail")).thenReturn(false);

        var resp = controller.checkAuth(ud);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
    }

    @Test
    void active_account_callsService_andRedirects() throws IOException {
        Long userId = 5L;
        String token = "t";
        var resp = controller.active_account(userId, token, response);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("account has been activated", resp.getBody());
        verify(authService).activateUser(userId, token);
        verify(response).sendRedirect("http://frontend/activated");
    }

    @Test
    void forgotPassword_callsService_andReturnsOk() throws Exception {
        var resp = controller.forgotPassword("a@b.com");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(authService).forgotPassword("a@b.com");
    }

    @Test
    void reset_password_callsService_andReturnsOk() {
        Long userId = 10L;
        String token = "tok";
        RestPasswordRequest r = new RestPasswordRequest();
        r.setPassword("password1");
        r.setPasswordConfirmation("password1");

        var resp = controller.reset_password(userId, token, r);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Password has been rested", resp.getBody());
        verify(authService).restPassword(userId, token, r);
    }
}
