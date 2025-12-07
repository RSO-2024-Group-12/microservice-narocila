package si.nakupify.entity;

/**
 * Lifecycle of an order.
 */
public enum OrderStatus {
    CREATED,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
