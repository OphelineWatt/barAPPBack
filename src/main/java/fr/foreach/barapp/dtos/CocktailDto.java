package fr.foreach.barapp.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CocktailDto {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String imageUrl;
    private boolean active;
    private Long createdById;
    private String createdAt;
    private String updatedAt;
    private List<CocktailIngredientDto> ingredients;
    private List<CocktailPriceDto> prices;
}
