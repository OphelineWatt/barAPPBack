package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.OrderItemResponse;
import fr.foreach.barapp.entities.Cocktail;
import fr.foreach.barapp.entities.ItemStatus;
import fr.foreach.barapp.entities.OrderItem;
import fr.foreach.barapp.entities.Size;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemMapperTest {

    private final OrderItemMapper mapper = Mappers.getMapper(OrderItemMapper.class);

    @Test
    void shouldMapEntityToDtoWithNestedFieldsAndEnumConversion() {
        Instant startedAt = Instant.parse("2026-01-01T10:00:00Z");
        OrderItem item = OrderItem.builder()
                .id(1L)
                .cocktail(Cocktail.builder().name("Mojito").build())
                .size(Size.builder().code("L").build())
                .quantity(2)
                .unitPrice(new BigDecimal("8.50"))
                .itemStatus(ItemStatus.ASSEMBLAGE)
                .startedAt(startedAt)
                .finishedAt(null)
                .build();

        OrderItemResponse dto = mapper.toDto(item);

        assertNotNull(dto);
        assertEquals("Mojito", dto.getCocktailName());
        assertEquals("L", dto.getSizeCode());
        assertEquals(new BigDecimal("8.50"), dto.getUnitPrice());
        assertEquals("ASSEMBLAGE", dto.getItemStatus());
        assertEquals("2026-01-01T10:00:00Z", dto.getStartedAt());
        assertNull(dto.getFinishedAt());
    }

    @Test
    void shouldReturnNullItemStatusWhenAbsent() {
        OrderItem item = OrderItem.builder().id(1L).itemStatus(null).build();

        OrderItemResponse dto = mapper.toDto(item);

        assertNull(dto.getItemStatus());
    }

    @Test
    void shouldReturnNullCocktailNameAndSizeCodeWhenNestedObjectsAbsent() {
        OrderItem item = OrderItem.builder().id(1L).cocktail(null).size(null).build();

        OrderItemResponse dto = mapper.toDto(item);

        assertNull(dto.getCocktailName());
        assertNull(dto.getSizeCode());
    }
}
