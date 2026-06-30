package fr.foreach.barapp.dtos;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long cocktailId;
    private String cocktailName;
    private Long sizeId;
    private String sizeCode;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String itemStatus;
    private String startedAt;
    private String finishedAt;
}
