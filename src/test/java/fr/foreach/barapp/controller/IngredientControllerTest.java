package fr.foreach.barapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.foreach.barapp.dtos.IngredientDto;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.services.IngredientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IngredientController.class)
@AutoConfigureMockMvc(addFilters = false)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IngredientService ingredientService;

    @Test
    void createShouldReturn201() throws Exception {
        IngredientDto dto = IngredientDto.builder().name("Rhum blanc").build();

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(ingredientService, times(1)).save(any(IngredientDto.class));
    }

    @Test
    void getShouldReturn200WithBody() throws Exception {
        IngredientDto dto = IngredientDto.builder().id(1L).name("Rhum blanc").build();
        when(ingredientService.find(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rhum blanc"));
    }

    @Test
    void getShouldReturn404WhenNotFound() throws Exception {
        when(ingredientService.find(99L)).thenThrow(new ResourceNotFoundException("Ingredient not found with id 99"));

        mockMvc.perform(get("/api/ingredients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listShouldReturn200WithIngredients() throws Exception {
        IngredientDto dto = IngredientDto.builder().id(1L).name("Rhum blanc").build();
        when(ingredientService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rhum blanc"));
    }

    @Test
    void updateShouldReturn204() throws Exception {
        IngredientDto dto = IngredientDto.builder().name("Rhum ambré").build();

        mockMvc.perform(put("/api/ingredients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(ingredientService, times(1)).update(eq(dto), eq(1L));
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/ingredients/1"))
                .andExpect(status().isNoContent());

        verify(ingredientService, times(1)).remove(1L);
    }
}
