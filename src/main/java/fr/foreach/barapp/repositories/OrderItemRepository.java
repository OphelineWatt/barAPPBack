package fr.foreach.barapp.repositories;

import fr.foreach.barapp.entities.OrderItem;
import fr.foreach.barapp.entities.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByItemStatus(ItemStatus itemStatus);
    List<OrderItem> findByItemStatusOrderByStartedAtAsc(ItemStatus itemStatus);
}
