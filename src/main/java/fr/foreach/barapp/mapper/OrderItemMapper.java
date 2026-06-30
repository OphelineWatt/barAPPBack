package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.OrderItemResponse;
import fr.foreach.barapp.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "cocktailName", source = "cocktail.name")
    @Mapping(target = "sizeCode", source = "size.code")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "itemStatus", expression = "java(item.getItemStatus() != null ? item.getItemStatus().name() : null)")
    @Mapping(target = "startedAt", expression = "java(item.getStartedAt() != null ? item.getStartedAt().toString() : null)")
    @Mapping(target = "finishedAt", expression = "java(item.getFinishedAt() != null ? item.getFinishedAt().toString() : null)")
    OrderItemResponse toDto(OrderItem item);
}
