package fr.foreach.barapp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
    private Long id;

    @NotBlank
    private String name;

    private String description;
}
