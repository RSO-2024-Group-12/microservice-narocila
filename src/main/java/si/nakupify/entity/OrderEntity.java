package si.nakupify.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderEntity extends PanacheEntity {

    @Column(name = "user_id", nullable = false)
    public Long userId;

    // Address snapshot at the time of ordering
    @Column(name = "recipient_name", length = 128)
    public String recipientName;
    @Column(length = 128)
    public String street;
    @Column(length = 64)
    public String houseNumber;
    @Column(length = 64)
    public String city;
    @Column(length = 16)
    public String postalCode;
    @Column(length = 64)
    public String country;

    @Column(name = "total_price_cents")
    public Long totalPriceCents;

    @Column(name = "shipping_cost_cents")
    public Long shippingCostCents;

    public Boolean paid = Boolean.FALSE;

    @Column(name = "payment_method", length = 32)
    public String paymentMethod; // e.g. CARD, COD, PAYPAL

    // shipment linkage
    @Column(name = "shipment_id")
    public Long shipmentId;

    @Column(name = "tracking_number", length = 64)
    public String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    public OrderStatus status = OrderStatus.CREATED;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt = Instant.now();
}
