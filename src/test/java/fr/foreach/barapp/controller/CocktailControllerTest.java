package fr.foreach.barapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.foreach.barapp.dtos.CocktailDto;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.services.CocktailService;
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

@WebMvcTest(controllers = CocktailController.class)
@AutoConfigureMockMvc(addFilters = false)
class CocktailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CocktailService cocktailService;

    @Test
    void createShouldReturn201() throws Exception {
        CocktailDto dto = CocktailDto.builder().name("Mojito").active(true).build();

        mockMvc.perform(post("/api/cocktails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(cocktailService, times(1)).save(any(CocktailDto.class));
    }

    @Test
    void getShouldReturn200WithBody() throws Exception {
        CocktailDto dto = CocktailDto.builder().id(1L).name("Mojito").build();
        when(cocktailService.find(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/cocktails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mojito"));
    }

    @Test
    void getShouldReturn404WhenNotFound() throws Exception {
        when(cocktailService.find(99L)).thenThrow(new ResourceNotFoundException("Cocktail not found with id 99"));

        mockMvc.perform(get("/api/cocktails/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listShouldReturn200WithCocktails() throws Exception {
        CocktailDto dto = CocktailDto.builder().id(1L).name("Mojito").build();
        when(cocktailService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/cocktails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mojito"));
    }

    @Test
    void updateShouldReturn204() throws Exception {
        CocktailDto dto = CocktailDto.builder().name("Mojito Updated").build();

        mockMvc.perform(put("/api/cocktails/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(cocktailService, times(1)).update(eq(dto), eq(1L));
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/cocktails/1"))
                .andExpect(status().isNoContent());

        verify(cocktailService, times(1)).remove(1L);
    }
}
