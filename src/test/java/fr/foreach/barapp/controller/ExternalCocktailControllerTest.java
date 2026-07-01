package fr.foreach.barapp.controller;

import fr.foreach.barapp.client.CocktailDbClient;
import fr.foreach.barapp.dtos.ExternalCocktailDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalCocktailController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExternalCocktailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CocktailDbClient cocktailDbClient;

    @Test
    void searchShouldReturnMatchingCocktails() throws Exception {
        List<ExternalCocktailDto> results = List.of(
                ExternalCocktailDto.builder()
                        .name("Mojito")
                        .imageUrl("https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg")
                        .ingredients(List.of("2 oz Light rum", "Juice of 1 Lime", "7 leaves Mint"))
                        .build()
        );
        when(cocktailDbClient.search("mojito")).thenReturn(results);

        mockMvc.perform(get("/api/external/cocktails/search?name=mojito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mojito"))
                .andExpect(jsonPath("$[0].imageUrl").isNotEmpty())
                .andExpect(jsonPath("$[0].ingredients[0]").value("2 oz Light rum"));
    }

    @Test
    void searchShouldReturnEmptyListWhenNoMatch() throws Exception {
        when(cocktailDbClient.search("xyzunknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/external/cocktails/search?name=xyzunknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchShouldReturnEmptyListWhenClientFails() throws Exception {
        when(cocktailDbClient.search("fail")).thenReturn(List.of());

        mockMvc.perform(get("/api/external/cocktails/search?name=fail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
