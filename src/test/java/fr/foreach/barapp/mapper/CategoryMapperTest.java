package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CategoryDto;
import fr.foreach.barapp.entities.Category;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private final CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    void shouldMapEntityToDto() {
        Category category = Category.builder().id(1L).name("Classiques").description("Cocktails classiques").build();

        CategoryDto dto = mapper.toDto(category);

        assertEquals(1L, dto.getId());
        assertEquals("Classiques", dto.getName());
        assertEquals("Cocktails classiques", dto.getDescription());
    }

    @Test
    void shouldMapDtoToEntity() {
        CategoryDto dto = CategoryDto.builder().id(1L).name("Tiki").description("Cocktails tiki").build();

        Category category = mapper.toEntity(dto);

        assertEquals(1L, category.getId());
        assertEquals("Tiki", category.getName());
        assertEquals("Cocktails tiki", category.getDescription());
    }

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
    }
}
