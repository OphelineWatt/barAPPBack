package fr.foreach.barapp.repositories;

import fr.foreach.barapp.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findByCode(String code);
}