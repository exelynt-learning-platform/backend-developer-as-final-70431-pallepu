package com.exelynt.booking.service;

import com.exelynt.booking.domain.Role;
import com.exelynt.booking.domain.User;
import com.exelynt.booking.dto.AuthResponse;
import com.exelynt.booking.dto.LoginRequest;
import com.exelynt.booking.dto.RegisterRequest;
import com.exelynt.booking.repository.UserRepository;
import com.exelynt.booking.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded_password")
                .fullName("John Doe")
                .role(Role.ROLE_USER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("user@example.com")
                .password("User@123")
                .build();

        registerRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("Password@123")
                .fullName("New User")
                .build();
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JWT token")
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("user@example.com", response.getEmail());
        assertEquals(Role.ROLE_USER, response.getRole());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when login authentication fails")
    void login_BadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Should register new user successfully")
    void register_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_pass");
        when(jwtUtils.generateTokenForUser(anyString(), anyString())).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals(registerRequest.getEmail(), response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when registering duplicate email")
    void register_DuplicateEmail() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }
}
