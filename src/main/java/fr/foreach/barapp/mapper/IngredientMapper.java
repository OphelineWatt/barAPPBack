package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.IngredientDto;
import fr.foreach.barapp.entities.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    IngredientDto toDto(Ingredient ingredient);

    Ingredient toEntity(IngredientDto dto);
}
