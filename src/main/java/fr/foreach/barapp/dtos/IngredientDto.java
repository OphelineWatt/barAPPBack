package fr.foreach.barapp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngredientDto {
    private Long id;

    @NotBlank
    private String name;

    private String unit;
}
