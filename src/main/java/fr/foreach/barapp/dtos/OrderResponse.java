package fr.foreach.barapp.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private String createdAt;
    private BigDecimal totalAmount;
    private String pickupCode;
    private List<OrderItemResponse> items;
}
