package fr.foreach.barapp.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CocktailDbClientTest {

    @Test
    void getIngredientsShouldCombineMeasureAndIngredient() {
        CocktailDbDrinkDto dto = new CocktailDbDrinkDto();
        dto.setExtra("strIngredient1", "Light rum");
        dto.setExtra("strMeasure1", "2 oz");
        dto.setExtra("strIngredient2", "Lime");
        dto.setExtra("strMeasure2", "Juice of 1");

        var ingredients = dto.getIngredients();

        assertEquals(2, ingredients.size());
        assertEquals("2 oz Light rum", ingredients.get(0));
        assertEquals("Juice of 1 Lime", ingredients.get(1));
    }

    @Test
    void getIngredientsShouldOmitMeasureWhenBlank() {
        CocktailDbDrinkDto dto = new CocktailDbDrinkDto();
        dto.setExtra("strIngredient1", "Mint");

        var ingredients = dto.getIngredients();

        assertEquals(1, ingredients.size());
        assertEquals("Mint", ingredients.get(0));
    }

    @Test
    void getIngredientsShouldSkipNullIngredients() {
        CocktailDbDrinkDto dto = new CocktailDbDrinkDto();
        dto.setExtra("strIngredient1", "Rum");
        // strIngredient2 non renseigné → ignoré

        assertEquals(1, dto.getIngredients().size());
    }

    @Test
    void setExtraShouldIgnoreNullAndBlankValues() {
        CocktailDbDrinkDto dto = new CocktailDbDrinkDto();
        dto.setExtra("strIngredient1", null);
        dto.setExtra("strIngredient2", "   ");

        assertTrue(dto.getIngredients().isEmpty());
    }
}
