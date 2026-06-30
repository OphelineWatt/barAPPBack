package fr.foreach.barapp.repositories;

import fr.foreach.barapp.entities.CocktailPrice;
import fr.foreach.barapp.entities.CocktailPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CocktailPriceRepository extends JpaRepository<CocktailPrice, CocktailPriceId> {
    List<CocktailPrice> findByCocktailId(Long cocktailId);
    Optional<CocktailPrice> findByCocktailIdAndSizeId(Long cocktailId, Long sizeId);
}