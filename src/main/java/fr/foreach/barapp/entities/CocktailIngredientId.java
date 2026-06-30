package fr.foreach.barapp.entities;

import java.io.Serializable;
import java.util.Objects;

public class CocktailIngredientId implements Serializable {
    private Long cocktailId;
    private Long ingredientId;

    public CocktailIngredientId() {}

    public CocktailIngredientId(Long cocktailId, Long ingredientId) {
        this.cocktailId = cocktailId;
        this.ingredientId = ingredientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CocktailIngredientId)) return false;
        CocktailIngredientId that = (CocktailIngredientId) o;
        return Objects.equals(cocktailId, that.cocktailId) && Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cocktailId, ingredientId);
    }
}
