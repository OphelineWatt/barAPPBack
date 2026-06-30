package fr.foreach.barapp.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CocktailPriceIdTest {

    @Test
    void equalsAndHashCodeShouldBeBasedOnBothFields() {
        CocktailPriceId a = new CocktailPriceId(1L, 2L);
        CocktailPriceId b = new CocktailPriceId(1L, 2L);
        CocktailPriceId different = new CocktailPriceId(1L, 3L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, different);
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "not-an-id");
    }

    @Test
    void noArgsConstructorShouldCreateEmptyInstance() {
        CocktailPriceId id = new CocktailPriceId();
        assertNotNull(id);
    }
}
