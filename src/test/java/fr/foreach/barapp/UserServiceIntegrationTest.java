package fr.foreach.barapp;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.repositories.UserRepository;
import fr.foreach.barapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@DisplayName("UserService Integration Tests with TestContainers")
class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("barapp_test")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données avant chaque test
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create user and retrieve it from database")
    void testCreateAndFindUser() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .email("integration@example.com")
                .name("Integration Test User")
                .password("password123")
                .role("CLIENT")
                .build();

        // Act
        UserResponse created = userService.create(request);

        // Assert
        assertNotNull(created.getId());
        UserResponse retrieved = userService.findById(created.getId());
        assertEquals("integration@example.com", retrieved.getEmail());
        assertEquals("Integration Test User", retrieved.getName());
        assertEquals("CLIENT", retrieved.getRole());
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAllUsers() {
        // Arrange - Créer plusieurs utilisateurs
        UserCreateRequest req1 = UserCreateRequest.builder()
                .email("user1@example.com")
                .name("User 1")
                .password("pass123")
                .role("CLIENT")
                .build();

        UserCreateRequest req2 = UserCreateRequest.builder()
                .email("user2@example.com")
                .name("User 2")
                .password("pass456")
                .role("ADMIN")
                .build();

        userService.create(req1);
        userService.create(req2);

        // Act
        var allUsers = userService.findAll();

        // Assert
        assertEquals(2, allUsers.size());
    }

    @Test
    @DisplayName("Should update existing user")
    void testUpdateUser() {
        // Arrange - Créer un utilisateur
        UserCreateRequest createReq = UserCreateRequest.builder()
                .email("update@example.com")
                .name("Original Name")
                .password("password123")
                .role("CLIENT")
                .build();

        UserResponse created = userService.create(createReq);

        // Act - Mettre à jour
        UserUpdateRequest updateReq = UserUpdateRequest.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        UserResponse updated = userService.update(created.getId(), updateReq);

        // Assert
        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());

        // Vérifier que la base de données a été mise à jour
        UserResponse retrieved = userService.findById(created.getId());
        assertEquals("Updated Name", retrieved.getName());
    }

    @Test
    @DisplayName("Should delete user from database")
    void testDeleteUser() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .email("delete@example.com")
                .name("To Delete")
                .password("password123")
                .role("CLIENT")
                .build();

        UserResponse created = userService.create(request);
        Long userId = created.getId();

        // Act
        userService.delete(userId);

        // Assert
        assertThrows(Exception.class, () -> userService.findById(userId));
    }

    @Test
    @DisplayName("Should prevent duplicate email")
    void testDuplicateEmailPrevention() {
        // Arrange
        UserCreateRequest req1 = UserCreateRequest.builder()
                .email("duplicate@example.com")
                .name("User 1")
                .password("pass123")
                .role("CLIENT")
                .build();

        UserCreateRequest req2 = UserCreateRequest.builder()
                .email("duplicate@example.com")
                .name("User 2")
                .password("pass456")
                .role("CLIENT")
                .build();

        userService.create(req1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.create(req2));
    }

    @Test
    @DisplayName("Should handle password encoding")
    void testPasswordEncoding() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .email("password@example.com")
                .name("Password Test")
                .password("mySecurePassword123!")
                .role("CLIENT")
                .build();

        // Act
        UserResponse response = userService.create(request);

        // Assert - Vérifier que l'utilisateur a été créé
        assertNotNull(response.getId());

        // Vérifier dans la BD que le mot de passe est hashé
        User savedUser = userRepository.findById(response.getId()).orElse(null);
        assertNotNull(savedUser);
        assertNotEquals("mySecurePassword123!", savedUser.getPasswordHash());
        assertTrue(savedUser.getPasswordHash().startsWith("$2a$")); // Format BCrypt
    }
}
