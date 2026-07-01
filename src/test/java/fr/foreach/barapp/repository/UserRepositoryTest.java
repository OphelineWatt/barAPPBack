package fr.foreach.barapp.repository;

import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.foreach.barapp_backend.BarappBackendApplication;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BarappBackendApplication.class)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
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
        // Action
        User saved = userRepository.save(testUser);

        // Vérification
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    @DisplayName("Should find user by id")
    void testFindUserById() {
        // Préparation
        User saved = userRepository.save(testUser);

        // Action
        Optional<User> found = userRepository.findById(saved.getId());

        // Vérification
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindUserByEmail() {
        // Préparation
        userRepository.save(testUser);

        // Action
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Vérification
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void testFindUserByEmailNotFound() {
        // Action
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Vérification
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should update user")
    void testUpdateUser() {
        // Préparation
        User saved = userRepository.save(testUser);
        saved.setName("Updated Name");
        saved.setEmail("updated@example.com");

        // Action
        User updated = userRepository.save(saved);

        // Vérification
        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("Should delete user")
    void testDeleteUser() {
        // Préparation
        User saved = userRepository.save(testUser);
        Long userId = saved.getId();

        // Action
        userRepository.deleteById(userId);

        // Vérification
        Optional<User> found = userRepository.findById(userId);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void testUniqueEmailConstraint() {
        // Préparation
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
                .role(Role.BARMAKER)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user1);

        // Action et vérification
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            userRepository.flush(); // Force la contrainte
        });
    }

    @Test
    @DisplayName("Should retrieve all users")
    void testFindAllUsers() {
        // Préparation
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
                .role(Role.BARMAKER)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // Action
        var allUsers = userRepository.findAll();

        // Vérification
        assertEquals(2, allUsers.size());
    }
}
