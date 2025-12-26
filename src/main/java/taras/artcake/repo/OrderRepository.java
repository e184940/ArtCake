package taras.artcake.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taras.artcake.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

