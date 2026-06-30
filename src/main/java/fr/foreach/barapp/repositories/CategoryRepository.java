package fr.foreach.barapp.repositories;

import fr.foreach.barapp.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
