package fr.foreach.barapp.repositories;

import fr.foreach.barapp.entities.CocktailIngredient;
import fr.foreach.barapp.entities.CocktailIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CocktailIngredientRepository extends JpaRepository<CocktailIngredient, CocktailIngredientId> {
    List<CocktailIngredient> findByCocktailId(Long cocktailId);
}
