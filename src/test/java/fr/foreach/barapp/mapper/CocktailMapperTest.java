package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CocktailDto;
import fr.foreach.barapp.dtos.CocktailIngredientDto;
import fr.foreach.barapp.dtos.CocktailPriceDto;
import fr.foreach.barapp.entities.Category;
import fr.foreach.barapp.entities.Cocktail;
import fr.foreach.barapp.entities.CocktailIngredient;
import fr.foreach.barapp.entities.CocktailPrice;
import fr.foreach.barapp.entities.Ingredient;
import fr.foreach.barapp.entities.Size;
import fr.foreach.barapp.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CocktailMapperTest {

    private final CocktailMapper mapper = Mappers.getMapper(CocktailMapper.class);

    @BeforeEach
    void wireSubMappers() {
        ReflectionTestUtils.setField(mapper, "cocktailIngredientMapper", Mappers.getMapper(CocktailIngredientMapper.class));
        ReflectionTestUtils.setField(mapper, "cocktailPriceMapper", Mappers.getMapper(CocktailPriceMapper.class));
    }

    @Test
    void shouldMapEntityToDtoAndDelegateNestedCollections() {
        Ingredient ingredient = Ingredient.builder().id(5L).name("Rhum blanc").build();
        Size size = Size.builder().id(100L).code("L").build();

        Cocktail cocktail = Cocktail.builder()
                .id(1L)
                .name("Mojito")
                .description("Rafraichissant")
                .imageUrl("http://example.com/mojito.png")
                .active(true)
                .category(Category.builder().id(7L).build())
                .createdBy(User.builder().id(9L).build())
                .createdAt(Instant.parse("2026-01-01T10:00:00Z"))
                .ingredients(List.of(CocktailIngredient.builder().ingredientId(5L).quantity("50ml").ingredient(ingredient).build()))
                .prices(List.of(CocktailPrice.builder().sizeId(100L).price(new BigDecimal("8.50")).size(size).build()))
                .build();

        CocktailDto dto = mapper.toDto(cocktail);

        assertNotNull(dto);
        assertEquals(7L, dto.getCategoryId());
        assertEquals(9L, dto.getCreatedById());
        assertTrue(dto.isActive());
        assertEquals("2026-01-01T10:00:00Z", dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());

        assertEquals(1, dto.getIngredients().size());
        assertEquals("Rhum blanc", dto.getIngredients().get(0).getIngredientName());

        assertEquals(1, dto.getPrices().size());
        assertEquals("L", dto.getPrices().get(0).getSizeCode());
    }

    @Test
    void shouldReturnNullCategoryAndCreatedByIdsWhenAbsent() {
        Cocktail cocktail = Cocktail.builder().id(1L).name("Mojito").build();

        CocktailDto dto = mapper.toDto(cocktail);

        assertNull(dto.getCategoryId());
        assertNull(dto.getCreatedById());
    }

    @Test
    void shouldMapDtoToEntityIgnoringNestedRelations() {
        // Le mapper ne recopie que les champs simples. Les relations
        // (category, createdBy, prices, ingredients) sont ignorées ici :
        // elles sont gérées dans CocktailService pour éviter des entités détachées.
        CocktailDto dto = CocktailDto.builder()
                .id(1L)
                .name("Mojito")
                .description("Rafraichissant")
                .imageUrl("http://example.com/mojito.png")
                .categoryId(7L)
                .createdById(9L)
                .active(true)
                .ingredients(List.of(CocktailIngredientDto.builder().ingredientId(5L).quantity("50ml").build()))
                .prices(List.of(CocktailPriceDto.builder().sizeId(100L).price(new BigDecimal("8.50")).build()))
                .build();

        Cocktail entity = mapper.toEntity(dto);

        assertNotNull(entity);
        // champs simples bien recopiés
        assertEquals("Mojito", entity.getName());
        assertEquals("Rafraichissant", entity.getDescription());
        assertTrue(entity.isActive());
        // relations ignorées par le mapper
        assertNull(entity.getCategory());
        assertNull(entity.getCreatedBy());
        assertTrue(entity.getIngredients().isEmpty());
        assertTrue(entity.getPrices().isEmpty());
    }
}
