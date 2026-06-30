package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CocktailDto;
import fr.foreach.barapp.entities.Cocktail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CocktailIngredientMapper.class, CocktailPriceMapper.class})
public interface CocktailMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "createdById", source = "createdBy.id")
    CocktailDto toDto(Cocktail cocktail);

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "createdBy.id", source = "createdById")
    Cocktail toEntity(CocktailDto dto);
}