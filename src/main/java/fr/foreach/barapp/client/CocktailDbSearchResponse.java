package fr.foreach.barapp.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class CocktailDbSearchResponse {
    private List<CocktailDbDrinkDto> drinks;
}
