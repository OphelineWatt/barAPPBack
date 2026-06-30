package fr.foreach.barapp.entities;

import java.io.Serializable;
import java.util.Objects;

public class CocktailPriceId implements Serializable {
    private Long cocktailId;
    private Long sizeId;

    public CocktailPriceId() {}

    public CocktailPriceId(Long cocktailId, Long sizeId) {
        this.cocktailId = cocktailId;
        this.sizeId = sizeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CocktailPriceId)) return false;
        CocktailPriceId that = (CocktailPriceId) o;
        return Objects.equals(cocktailId, that.cocktailId) && Objects.equals(sizeId, that.sizeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cocktailId, sizeId);
    }
}
