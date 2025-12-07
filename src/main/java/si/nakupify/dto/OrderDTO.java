package si.nakupify.dto;

import si.nakupify.entity.OrderEntity;
import si.nakupify.entity.OrderItemEntity;
import si.nakupify.entity.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderDTO(
        Long id,
        Long userId,
        String recipientName,
        String street,
        String houseNumber,
        String city,
        String postalCode,
        String country,
        Long totalPriceCents,
        Long shippingCostCents,
        Boolean paid,
        String paymentMethod,
        Long shipmentId,
        String trackingNumber,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemDTO> items
) {
    public static OrderDTO from(OrderEntity e, List<OrderItemEntity> items) {
        return new OrderDTO(
                e.id,
                e.userId,
                e.recipientName,
                e.street,
                e.houseNumber,
                e.city,
                e.postalCode,
                e.country,
                e.totalPriceCents,
                e.shippingCostCents,
                e.paid,
                e.paymentMethod,
                e.shipmentId,
                e.trackingNumber,
                e.status,
                e.createdAt,
                e.updatedAt,
                items == null ? List.of() : items.stream().map(OrderItemDTO::from).toList()
        );
    }
}
