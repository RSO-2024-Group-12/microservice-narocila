package si.nakupify.dto;

import si.nakupify.entity.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderDto(
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
        List<OrderItemDto> items
) {
}
