package fr.foreach.barapp.repository;

import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

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
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .name("Test User")
                .passwordHash("$2a$10$encrypted")
                .role(Role.CLIENT)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should save user to database")
    void testSaveUser() {
        // Act
        User saved = userRepository.save(testUser);

        // Assert
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    @DisplayName("Should find user by id")
    void testFindUserById() {
        // Arrange
        User saved = userRepository.save(testUser);

        // Act
        Optional<User> found = userRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindUserByEmail() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void testFindUserByEmailNotFound() {
        // Act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should update user")
    void testUpdateUser() {
        // Arrange
        User saved = userRepository.save(testUser);
        saved.setName("Updated Name");
        saved.setEmail("updated@example.com");

        // Act
        User updated = userRepository.save(saved);

        // Assert
        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("Should delete user")
    void testDeleteUser() {
        // Arrange
        User saved = userRepository.save(testUser);
        Long userId = saved.getId();

        // Act
        userRepository.deleteById(userId);

        // Assert
        Optional<User> found = userRepository.findById(userId);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void testUniqueEmailConstraint() {
        // Arrange
        User user1 = User.builder()
                .email("unique@example.com")
                .name("User 1")
                .passwordHash("hash1")
                .role(Role.CLIENT)
                .createdAt(Instant.now())
                .build();

        User user2 = User.builder()
                .email("unique@example.com")
                .name("User 2")
                .passwordHash("hash2")
                .role(Role.ADMIN)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user1);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            userRepository.flush(); // Force la contrainte
        });
    }

    @Test
    @DisplayName("Should retrieve all users")
    void testFindAllUsers() {
        // Arrange
        User user1 = User.builder()
                .email("user1@example.com")
                .name("User 1")
                .passwordHash("hash1")
                .role(Role.CLIENT)
                .createdAt(Instant.now())
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .name("User 2")
                .passwordHash("hash2")
                .role(Role.ADMIN)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        var allUsers = userRepository.findAll();

        // Assert
        assertEquals(2, allUsers.size());
    }
}
