package fr.foreach.barapp.repositories;

import fr.foreach.barapp.entities.Order;
import fr.foreach.barapp.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
