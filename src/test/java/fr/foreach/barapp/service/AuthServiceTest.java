package fr.foreach.barapp.service;

import fr.foreach.barapp.dtos.LoginRequest;
import fr.foreach.barapp.dtos.LoginResponse;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.repositories.UserRepository;
import fr.foreach.barapp.security.JwtService;
import fr.foreach.barapp.services.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, passwordEncoder, jwtService);

        testUser = User.builder()
                .id(1L)
                .email("client@example.com")
                .name("Client")
                .passwordHash("hashed-password")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    @DisplayName("login should return a token when credentials are valid")
    void testLoginSuccess() {
        LoginRequest request = LoginRequest.builder().email("client@example.com").password("secret").build();
        when(userRepository.findByEmail("client@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("secret", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("CLIENT", response.getRole());
    }

    @Test
    @DisplayName("login should throw BadCredentialsException when user not found")
    void testLoginUserNotFound() {
        LoginRequest request = LoginRequest.builder().email("unknown@example.com").password("secret").build();
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("login should throw BadCredentialsException when password does not match")
    void testLoginWrongPassword() {
        LoginRequest request = LoginRequest.builder().email("client@example.com").password("wrong").build();
        when(userRepository.findByEmail("client@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
