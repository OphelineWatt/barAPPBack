package fr.foreach.barapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.foreach.barapp.dtos.CategoryDto;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.services.CategoryService;
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

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void createShouldReturn201() throws Exception {
        CategoryDto dto = CategoryDto.builder().name("Classiques").build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(categoryService, times(1)).save(any(CategoryDto.class));
    }

    @Test
    void getShouldReturn200WithBody() throws Exception {
        CategoryDto dto = CategoryDto.builder().id(1L).name("Classiques").build();
        when(categoryService.find(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Classiques"));
    }

    @Test
    void getShouldReturn404WhenNotFound() throws Exception {
        when(categoryService.find(99L)).thenThrow(new ResourceNotFoundException("Category not found with id 99"));

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listShouldReturn200WithCategories() throws Exception {
        CategoryDto dto = CategoryDto.builder().id(1L).name("Classiques").build();
        when(categoryService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Classiques"));
    }

    @Test
    void updateShouldReturn204() throws Exception {
        CategoryDto dto = CategoryDto.builder().name("Tiki").build();

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).update(eq(dto), eq(1L));
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).remove(1L);
    }
}
