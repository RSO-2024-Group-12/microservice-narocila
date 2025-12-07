package si.nakupify.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItemEntity extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    public OrderEntity order;

    @Column(name = "product_id", nullable = false)
    public Long productId;

    @Column(nullable = false)
    public Integer quantity;

    @Column(name = "unit_price_cents", nullable = false)
    public Long unitPriceCents;

    @Column(name = "total_price_cents", nullable = false)
    public Long totalPriceCents;
}
