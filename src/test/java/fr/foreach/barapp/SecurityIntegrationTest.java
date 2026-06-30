package fr.foreach.barapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.foreach.barapp.dtos.CategoryDto;
import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.repositories.UserRepository;
import fr.foreach.barapp.services.UserService;
import fr.foreach.barapp_backend.BarappBackendApplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BarappBackendApplication.class)
@AutoConfigureMockMvc
@DisplayName("Security / role enforcement Integration Tests")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userService.create(UserCreateRequest.builder()
                .email("barmaker@example.com")
                .name("Bar Maker")
                .password("password123")
                .role("BARMAKER")
                .build());

        userService.create(UserCreateRequest.builder()
                .email("client@example.com")
                .name("Client")
                .password("password123")
                .role("CLIENT")
                .build());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    @DisplayName("GET /api/cocktails is public")
    void publicCatalogIsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/cocktails"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Creating a category without a token is rejected")
    void creatingCategoryWithoutTokenIsRejected() throws Exception {
        CategoryDto dto = CategoryDto.builder().name("Tiki").build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 401 && status != 403) {
                        throw new AssertionError("Expected 401 or 403 but got " + status);
                    }
                });
    }

    @Test
    @DisplayName("Creating a category as CLIENT is forbidden")
    void creatingCategoryAsClientIsForbidden() throws Exception {
        String token = loginAndGetToken("client@example.com", "password123");
        CategoryDto dto = CategoryDto.builder().name("Tiki").build();

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Creating a category as BARMAKER succeeds")
    void creatingCategoryAsBarmakerSucceeds() throws Exception {
        String token = loginAndGetToken("barmaker@example.com", "password123");
        CategoryDto dto = CategoryDto.builder().name("Tiki").build();

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Listing orders as CLIENT is forbidden (barmaker-only queue)")
    void listingOrdersAsClientIsForbidden() throws Exception {
        String token = loginAndGetToken("client@example.com", "password123");

        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Listing orders as BARMAKER succeeds")
    void listingOrdersAsBarmakerSucceeds() throws Exception {
        String token = loginAndGetToken("barmaker@example.com", "password123");

        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
