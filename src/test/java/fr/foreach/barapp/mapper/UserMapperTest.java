package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapCreateRequestToUser() {
        UserCreateRequest request = UserCreateRequest.builder()
                .email("alice@example.com")
                .name("Alice")
                .password("secret123")
                .role("BARMAKER")
                .build();

        User user = mapper.toEntity(request);

        assertNotNull(user);
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("Alice", user.getName());
        assertEquals(Role.BARMAKER, user.getRole());
        assertEquals(null, user.getPasswordHash());
    }

    @Test
    void shouldMapUserToResponse() {
        User user = User.builder()
                .id(1L)
                .email("bob@example.com")
                .name("Bob")
                .role(Role.CLIENT)
                .build();

        UserResponse response = mapper.toResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("bob@example.com", response.getEmail());
        assertEquals("Bob", response.getName());
        assertEquals("CLIENT", response.getRole());
    }
}
