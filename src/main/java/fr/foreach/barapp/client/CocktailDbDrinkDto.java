package fr.foreach.barapp.client;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class CocktailDbDrinkDto {

    @JsonProperty("strDrink")
    private String name;

    @JsonProperty("strDrinkThumb")
    private String imageUrl;

    private final Map<String, String> extras = new HashMap<>();

    @JsonAnySetter
    void setExtra(String key, Object value) {
        if (value != null && !value.toString().isBlank()) {
            extras.put(key, value.toString().trim());
        }
    }

    List<String> getIngredients() {
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String ingredient = extras.get("strIngredient" + i);
            if (ingredient == null || ingredient.isBlank()) continue;
            String measure = extras.getOrDefault("strMeasure" + i, "").trim();
            result.add(measure.isEmpty() ? ingredient : measure + " " + ingredient);
        }
        return result;
    }
}
