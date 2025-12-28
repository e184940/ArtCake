package taras.artcake.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taras.artcake.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Sorterer på leveringsdato (hvis deliveryDate er lagret som String YYYY-MM-DD fungerer dette bra,
    // hvis det er norsk format DD.MM.YYYY vil sorteringen bli litt rar i SQL, men vi kan fikse det i Java)
    List<Order> findAllByOrderByDeliveryDateAsc();

    // Sorterer på når bestillingen ble lagt inn
    List<Order> findAllByOrderByOrderDateDesc();

    // Filtrer på status (f.eks. "Ny", "Levert")
    List<Order> findByStatus(String status);
}
