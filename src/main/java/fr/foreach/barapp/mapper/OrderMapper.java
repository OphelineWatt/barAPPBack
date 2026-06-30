package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.OrderCreateRequest;
import fr.foreach.barapp.dtos.OrderResponse;
import fr.foreach.barapp.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderResponse toDto(Order order);

    // No direct mapping from create request to entity because items need price lookup
}
