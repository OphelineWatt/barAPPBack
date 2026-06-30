package fr.foreach.barapp.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cocktail_ingredients")
@IdClass(CocktailIngredientId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocktailIngredient {
    @Id
    @Column(name = "cocktail_id")
    private Long cocktailId;

    @Id
    @Column(name = "ingredient_id")
    private Long ingredientId;

    private String quantity;

    @ManyToOne
    @JoinColumn(name = "cocktail_id", insertable = false, updatable = false)
    private Cocktail cocktail;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", insertable = false, updatable = false)
    private Ingredient ingredient;
}
