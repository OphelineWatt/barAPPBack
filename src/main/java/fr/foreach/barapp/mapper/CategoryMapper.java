package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.CategoryDto;
import fr.foreach.barapp.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto dto);
}
