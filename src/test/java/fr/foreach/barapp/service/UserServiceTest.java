package fr.foreach.barapp.service;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialiser les données de test
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .passwordHash("$2a$10$encrypted")
                .role(Role.CLIENT)
                .createdAt(Instant.now())
                .build();

        createRequest = UserCreateRequest.builder()
                .email("newuser@example.com")
                .name("New User")
                .password("password123")
                .role("CLIENT")
                .build();
    }

    @Test
    @DisplayName("findAll should return list of users")
    void testFindAll() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserResponse> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById should return user when found")
    void testFindByIdSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById should throw ResourceNotFoundException when user not found")
    void testFindByIdNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("create should save new user successfully")
    void testCreateSuccess() {
        // Arrange
        when(userRepository.findByEmail(createRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encrypted");

        User savedUser = User.builder()
                .id(2L)
                .email(createRequest.getEmail())
                .name(createRequest.getName())
                .passwordHash("$2a$10$encrypted")
                .role(Role.CLIENT)
                .createdAt(Instant.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse result = userService.create(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail(createRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("create should throw exception when email already exists")
    void testCreateDuplicateEmail() {
        // Arrange
        when(userRepository.findByEmail(createRequest.getEmail()))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.create(createRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should modify user successfully")
    void testUpdateSuccess() {
        // Arrange
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("updated@example.com")
                .name("Updated Name")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponse result = userService.update(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("update should throw exception when user not found")
    void testUpdateNotFound() {
        // Arrange
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name("Updated Name")
                .build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.update(99L, updateRequest));
    }

    @Test
    @DisplayName("delete should remove user successfully")
    void testDeleteSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        userService.delete(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("delete should throw exception when user not found")
    void testDeleteNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(99L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("toResponse should convert User entity to UserResponse")
    void testToResponse() {
        // Act
        UserResponse result = userService.toResponse(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getName(), result.getName());
        assertEquals("CLIENT", result.getRole());
    }
}
