package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CocktailIngredientDto;
import fr.foreach.barapp.entities.CocktailIngredient;
import fr.foreach.barapp.entities.Ingredient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class CocktailIngredientMapperTest {

    private final CocktailIngredientMapper mapper = Mappers.getMapper(CocktailIngredientMapper.class);

    @Test
    void shouldMapEntityToDtoAndExtractIngredientName() {
        Ingredient ingredient = Ingredient.builder().id(5L).name("Rhum blanc").unit("ml").build();
        CocktailIngredient entity = CocktailIngredient.builder()
                .cocktailId(1L)
                .ingredientId(5L)
                .quantity("50ml")
                .ingredient(ingredient)
                .build();

        CocktailIngredientDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(5L, dto.getIngredientId());
        assertEquals("Rhum blanc", dto.getIngredientName());
        assertEquals("50ml", dto.getQuantity());
    }

    @Test
    void shouldReturnNullIngredientNameWhenIngredientIsNull() {
        CocktailIngredient entity = CocktailIngredient.builder()
                .cocktailId(1L)
                .ingredientId(5L)
                .quantity("50ml")
                .ingredient(null)
                .build();

        CocktailIngredientDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertNull(dto.getIngredientName());
    }
}
