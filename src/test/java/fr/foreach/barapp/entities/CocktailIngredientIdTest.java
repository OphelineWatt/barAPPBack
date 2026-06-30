package fr.foreach.barapp.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CocktailIngredientIdTest {

    @Test
    void equalsAndHashCodeShouldBeBasedOnBothFields() {
        CocktailIngredientId a = new CocktailIngredientId(1L, 2L);
        CocktailIngredientId b = new CocktailIngredientId(1L, 2L);
        CocktailIngredientId different = new CocktailIngredientId(1L, 3L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, different);
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "not-an-id");
    }

    @Test
    void noArgsConstructorShouldCreateEmptyInstance() {
        CocktailIngredientId id = new CocktailIngredientId();
        assertNotNull(id);
    }
}
