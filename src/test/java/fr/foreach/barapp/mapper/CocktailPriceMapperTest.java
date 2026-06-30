package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CocktailPriceDto;
import fr.foreach.barapp.entities.CocktailPrice;
import fr.foreach.barapp.entities.Size;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CocktailPriceMapperTest {

    private final CocktailPriceMapper mapper = Mappers.getMapper(CocktailPriceMapper.class);

    @Test
    void shouldMapEntityToDtoAndExtractSizeCode() {
        Size size = Size.builder().id(100L).code("L").label("Large").build();
        CocktailPrice entity = CocktailPrice.builder()
                .cocktailId(1L)
                .sizeId(100L)
                .price(new BigDecimal("8.50"))
                .size(size)
                .build();

        CocktailPriceDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(100L, dto.getSizeId());
        assertEquals("L", dto.getSizeCode());
        assertEquals(new BigDecimal("8.50"), dto.getPrice());
    }

    @Test
    void shouldReturnNullSizeCodeWhenSizeIsNull() {
        CocktailPrice entity = CocktailPrice.builder()
                .cocktailId(1L)
                .sizeId(100L)
                .price(new BigDecimal("8.50"))
                .size(null)
                .build();

        CocktailPriceDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertNull(dto.getSizeCode());
    }
}
