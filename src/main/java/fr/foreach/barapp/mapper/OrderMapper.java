package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.OrderResponse;
import fr.foreach.barapp.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderResponse toDto(Order order);

    // pas de mapping direct depuis la requête de création : il faut d'abord aller chercher le prix de chaque cocktail
}
