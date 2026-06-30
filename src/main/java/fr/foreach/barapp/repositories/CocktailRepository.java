package fr.foreach.barapp.repositories;

import fr.foreach.barapp.entities.Cocktail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CocktailRepository extends JpaRepository<Cocktail, Long> {
    List<Cocktail> findByActiveTrue();
    List<Cocktail> findByCategoryId(Long categoryId);
}