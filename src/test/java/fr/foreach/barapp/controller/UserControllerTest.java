package fr.foreach.barapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createShouldReturn201() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .email("alice@example.com")
                .password("secret123")
                .name("Alice")
                .role("CLIENT")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).save(any(UserCreateRequest.class));
    }

    @Test
    void getShouldReturn200WithBody() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("alice@example.com")
                .name("Alice")
                .role("CLIENT")
                .createdAt(Instant.parse("2026-01-01T10:00:00Z"))
                .build();
        when(userService.find(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getShouldReturn404WhenNotFound() throws Exception {
        when(userService.find(99L)).thenThrow(new ResourceNotFoundException("User not found with id 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listShouldReturn200WithUsers() throws Exception {
        UserResponse response = UserResponse.builder().id(1L).email("alice@example.com").build();
        when(userService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void updateShouldReturn204() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder().name("Updated").build();

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UserUpdateRequest> captor = ArgumentCaptor.forClass(UserUpdateRequest.class);
        verify(userService, times(1)).update(captor.capture(), eq(1L));
        assertEquals("Updated", captor.getValue().getName());
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).remove(1L);
    }

    @Test
    void createShouldReturn400WhenEmailAlreadyExists() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .email("alice@example.com")
                .password("secret123")
                .name("Alice")
                .build();
        doThrow(new IllegalArgumentException("Email already exists")).when(userService).save(any(UserCreateRequest.class));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
