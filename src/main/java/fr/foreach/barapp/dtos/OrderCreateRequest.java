package fr.foreach.barapp.dtos;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreateRequest {
    private Long userId;
    private List<OrderItemRequest> items;
}
