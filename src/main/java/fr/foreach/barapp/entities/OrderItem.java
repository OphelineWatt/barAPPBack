package fr.foreach.barapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Cocktail cocktail;

    @ManyToOne
    private Size size;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status")
    private ItemStatus itemStatus = ItemStatus.PREPARATION_INGREDIENTS;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
