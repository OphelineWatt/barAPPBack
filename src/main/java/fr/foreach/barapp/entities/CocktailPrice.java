package fr.foreach.barapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cocktail_price")
@IdClass(CocktailPriceId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocktailPrice {
    @Id
    @Column(name = "cocktail_id")
    private Long cocktailId;

    @Id
    @Column(name = "size_id")
    private Long sizeId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "cocktail_id", insertable = false, updatable = false)
    private Cocktail cocktail;

    @ManyToOne
    @JoinColumn(name = "size_id", insertable = false, updatable = false)
    private Size size;
}
