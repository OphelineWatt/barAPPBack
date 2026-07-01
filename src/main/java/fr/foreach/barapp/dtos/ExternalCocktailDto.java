package fr.foreach.barapp.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExternalCocktailDto {
    private String name;
    private String imageUrl;
    private List<String> ingredients;
}
