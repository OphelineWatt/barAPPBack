package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CocktailIngredientDto;
import fr.foreach.barapp.entities.CocktailIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CocktailIngredientMapper {
    @Mapping(target = "ingredientName", source = "ingredient.name")
    CocktailIngredientDto toDto(CocktailIngredient entity);
}
