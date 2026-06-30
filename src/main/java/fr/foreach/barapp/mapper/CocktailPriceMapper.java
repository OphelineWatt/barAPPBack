package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CocktailPriceDto;
import fr.foreach.barapp.entities.CocktailPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CocktailPriceMapper {
    @Mapping(target = "sizeCode", source = "size.code")
    CocktailPriceDto toDto(CocktailPrice entity);
}
