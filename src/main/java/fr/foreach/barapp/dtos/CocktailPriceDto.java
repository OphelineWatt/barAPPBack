package fr.foreach.barapp.dtos;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CocktailPriceDto {
    private Long sizeId;
    private String sizeCode;
    private BigDecimal price;
}
