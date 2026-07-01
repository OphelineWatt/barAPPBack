package fr.foreach.barapp.service;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.UserMapper;
import fr.foreach.barapp.repositories.UserRepository;
import fr.foreach.barapp.services.UserService;

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

    @Mock
    private UserMapper userMapper;

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

        when(userMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole() != null ? user.getRole().name() : null)
                    .createdAt(user.getCreatedAt())
                    .build();
        });

        when(userMapper.toEntity(any(UserCreateRequest.class))).thenAnswer(invocation -> {
            UserCreateRequest request = invocation.getArgument(0);
            return User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .role(request.getRole() != null ? Role.valueOf(request.getRole()) : Role.CLIENT)
                    .build();
        });
    }

    @Test
    @DisplayName("findAll should return list of users")
    void testFindAll() {
        // Préparation
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Action
        List<UserResponse> result = userService.findAll();

        // Vérification
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById should return user when found")
    void testFindByIdSuccess() {
        // Préparation
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Action
        UserResponse result = userService.findById(1L);

        // Vérification
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById should throw ResourceNotFoundException when user not found")
    void testFindByIdNotFound() {
        // Préparation
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Action et vérification
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("create should save new user successfully")
    void testCreateSuccess() {
        // Préparation
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

        // Action
        UserResponse result = userService.create(createRequest);

        // Vérification
        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail(createRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("create should throw exception when email already exists")
    void testCreateDuplicateEmail() {
        // Préparation
        when(userRepository.findByEmail(createRequest.getEmail()))
                .thenReturn(Optional.of(testUser));

        // Action et vérification
        assertThrows(IllegalArgumentException.class, () -> userService.create(createRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should modify user successfully")
    void testUpdateSuccess() {
        // Préparation
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("updated@example.com")
                .name("Updated Name")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Action
        UserResponse result = userService.update(1L, updateRequest);

        // Vérification
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("update should throw exception when user not found")
    void testUpdateNotFound() {
        // Préparation
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name("Updated Name")
                .build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Action et vérification
        assertThrows(ResourceNotFoundException.class, () -> userService.update(99L, updateRequest));
    }

    @Test
    @DisplayName("delete should remove user successfully")
    void testDeleteSuccess() {
        // Préparation
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Action
        userService.delete(1L);

        // Vérification
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("delete should throw exception when user not found")
    void testDeleteNotFound() {
        // Préparation
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Action et vérification
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(99L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("toResponse should convert User entity to UserResponse")
    void testToResponse() {
        // Action
        UserResponse result = userService.toResponse(testUser);

        // Vérification
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getName(), result.getName());
        assertEquals("CLIENT", result.getRole());
    }
}
