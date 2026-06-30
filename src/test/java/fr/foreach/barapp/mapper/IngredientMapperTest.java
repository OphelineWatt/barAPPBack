package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.IngredientDto;
import fr.foreach.barapp.entities.Ingredient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class IngredientMapperTest {

    private final IngredientMapper mapper = Mappers.getMapper(IngredientMapper.class);

    @Test
    void shouldMapEntityToDto() {
        Ingredient ingredient = Ingredient.builder().id(1L).name("Rhum blanc").unit("cl").build();

        IngredientDto dto = mapper.toDto(ingredient);

        assertEquals(1L, dto.getId());
        assertEquals("Rhum blanc", dto.getName());
        assertEquals("cl", dto.getUnit());
    }

    @Test
    void shouldMapDtoToEntity() {
        IngredientDto dto = IngredientDto.builder().id(1L).name("Menthe").unit("feuilles").build();

        Ingredient ingredient = mapper.toEntity(dto);

        assertEquals(1L, ingredient.getId());
        assertEquals("Menthe", ingredient.getName());
        assertEquals("feuilles", ingredient.getUnit());
    }

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
    }
}
