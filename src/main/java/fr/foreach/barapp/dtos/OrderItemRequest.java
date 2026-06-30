package fr.foreach.barapp.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemRequest {
    private Long cocktailId;
    private Long sizeId;
    private Integer quantity;
}
