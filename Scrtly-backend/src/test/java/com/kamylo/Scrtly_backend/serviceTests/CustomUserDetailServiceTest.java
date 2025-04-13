package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    private final String email = "test@example.com";

    @Test
    void testLoadUserByUsername_UserFound() {
        UserEntity dummyUser = new UserEntity();
        dummyUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(dummyUser));

        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailService.loadUserByUsername(email));
        assertEquals(email, ex.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
}