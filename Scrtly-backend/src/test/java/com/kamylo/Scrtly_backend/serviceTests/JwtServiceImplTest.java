
package com.kamylo.Scrtly_backend.serviceTests;
import com.kamylo.Scrtly_backend.auth.service.impl.JwtServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @Mock
    private UserDetails userDetails;

    private final String secretKey = Base64.getEncoder().encodeToString("01234567890123456789012345678901".getBytes());
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secretkey", secretKey);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(username);
        assertNotNull(token);
        assertEquals(username, jwtService.extractUserName(token));
    }

    @Test
    void testExtractUserName() {
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUserName(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken() {
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        String token = jwtService.generateToken(username);
        assertTrue(jwtService.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();
        assertFalse(jwtService.validateJwtToken(expiredToken));
    }

    @Test
    void testValidateJwtToken_InvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtService.validateJwtToken(invalidToken));
    }

    @Test
    void testValidateToken_UsernameMismatch() {
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn("innyUzytkownik");
        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testValidateToken_ExpiredToken() {
       SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 5000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();
        when(userDetails.getUsername()).thenReturn(username);
        try {
            boolean result = jwtService.validateToken(expiredToken, userDetails);
            assertFalse(result);
        } catch (ExpiredJwtException e) {
            assertTrue(true);
        }
    }
    
    @Test
    void testValidateJwtToken_UnsupportedJwtException() {
        try (MockedStatic<Jwts> mockedJwts = mockStatic(Jwts.class)) {
            JwtParserBuilder parserBuilderMock = mock(JwtParserBuilder.class);
            JwtParser jwtParserMock = mock(JwtParser.class);
            mockedJwts.when(Jwts::parser).thenReturn(parserBuilderMock);
            when(parserBuilderMock.verifyWith(any(SecretKey.class))).thenReturn(parserBuilderMock);
            when(parserBuilderMock.build()).thenReturn(jwtParserMock);
            when(jwtParserMock.parseSignedClaims(anyString()))
                    .thenThrow(new UnsupportedJwtException("Unsupported token"));
            assertFalse(jwtService.validateJwtToken("dummyToken"));
        }
    }
    
    @Test
    void testValidateJwtToken_IllegalArgumentException() {
        try (MockedStatic<Jwts> mockedJwts = mockStatic(Jwts.class)) {
            JwtParserBuilder parserBuilderMock = mock(JwtParserBuilder.class);
            JwtParser jwtParserMock = mock(JwtParser.class);
            mockedJwts.when(Jwts::parser).thenReturn(parserBuilderMock);
            when(parserBuilderMock.verifyWith(any(SecretKey.class))).thenReturn(parserBuilderMock);
            when(parserBuilderMock.build()).thenReturn(jwtParserMock);
            when(jwtParserMock.parseSignedClaims(anyString()))
                    .thenThrow(new IllegalArgumentException("Empty token"));
            assertFalse(jwtService.validateJwtToken("dummyToken"));
        }
    }
}