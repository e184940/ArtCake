package taras.artcake.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import taras.artcake.model.Allergen;

public interface AllergenRepo extends JpaRepository<Allergen, Integer> {
}
