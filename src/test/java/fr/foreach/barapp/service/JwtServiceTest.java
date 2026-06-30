package fr.foreach.barapp.service;

import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("dev-only-barapp-secret-key-change-me-in-production-32bytes", 86400000L);
    }

    @Test
    @DisplayName("generateToken should produce a token from which the email can be extracted")
    void testGenerateAndExtractEmail() {
        User user = User.builder().id(1L).email("client@example.com").role(Role.CLIENT).build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtService.isValid(token));
        assertEquals("client@example.com", jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("isValid should return false for a garbage token")
    void testInvalidToken() {
        assertFalse(jwtService.isValid("not-a-real-token"));
    }

    @Test
    @DisplayName("isValid should return false for a token signed with a different secret")
    void testTokenSignedWithDifferentSecret() {
        User user = User.builder().id(1L).email("client@example.com").role(Role.CLIENT).build();
        String token = jwtService.generateToken(user);

        JwtService otherJwtService = new JwtService("another-completely-different-secret-key-for-barapp-32bytes", 86400000L);

        assertFalse(otherJwtService.isValid(token));
    }
}
