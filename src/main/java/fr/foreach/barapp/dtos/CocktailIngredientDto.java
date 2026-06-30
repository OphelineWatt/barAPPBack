package fr.foreach.barapp.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CocktailIngredientDto {
    private Long ingredientId;
    private String ingredientName;
    private String quantity;
}
