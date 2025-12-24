package taras.artcake.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taras.artcake.model.Cake;

@Repository
public interface CakeRepo extends JpaRepository<Cake, Integer> {
}