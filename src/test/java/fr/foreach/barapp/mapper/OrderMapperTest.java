package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.OrderResponse;
import fr.foreach.barapp.entities.Cocktail;
import fr.foreach.barapp.entities.ItemStatus;
import fr.foreach.barapp.entities.Order;
import fr.foreach.barapp.entities.OrderItem;
import fr.foreach.barapp.entities.OrderStatus;
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

class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @BeforeEach
    void wireSubMapper() {
        ReflectionTestUtils.setField(mapper, "orderItemMapper", Mappers.getMapper(OrderItemMapper.class));
    }

    @Test
    void shouldMapEntityToDtoAndDelegateItems() {
        OrderItem item = OrderItem.builder()
                .id(1L)
                .cocktail(Cocktail.builder().name("Mojito").build())
                .size(Size.builder().code("L").build())
                .quantity(2)
                .unitPrice(new BigDecimal("8.50"))
                .itemStatus(ItemStatus.PREPARATION_INGREDIENTS)
                .build();

        Order order = Order.builder()
                .id(1L)
                .user(User.builder().id(3L).build())
                .status(OrderStatus.COMMANDEE)
                .createdAt(Instant.parse("2026-01-01T10:00:00Z"))
                .totalAmount(new BigDecimal("17.00"))
                .pickupCode("ABC123")
                .items(List.of(item))
                .build();

        OrderResponse dto = mapper.toDto(order);

        assertNotNull(dto);
        assertEquals(3L, dto.getUserId());
        assertEquals("COMMANDEE", dto.getStatus());
        assertEquals("2026-01-01T10:00:00Z", dto.getCreatedAt());
        assertEquals("ABC123", dto.getPickupCode());
        assertEquals(new BigDecimal("17.00"), dto.getTotalAmount());

        assertEquals(1, dto.getItems().size());
        assertEquals("Mojito", dto.getItems().get(0).getCocktailName());
    }

    @Test
    void shouldReturnNullUserIdWhenUserAbsent() {
        Order order = Order.builder().id(1L).user(null).build();

        OrderResponse dto = mapper.toDto(order);

        assertNull(dto.getUserId());
    }
}
