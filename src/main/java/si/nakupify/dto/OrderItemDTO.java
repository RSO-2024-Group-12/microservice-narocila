package si.nakupify.dto;

import si.nakupify.entity.OrderItemEntity;

public record OrderItemDTO(
        Long id,
        Long productId,
        Integer quantity,
        Long unitPriceCents,
        Long totalPriceCents
) {
    public static OrderItemDTO from(OrderItemEntity e) {
        if (e == null) return null;
        return new OrderItemDTO(
                e.id,
                e.productId,
                e.quantity,
                e.unitPriceCents,
                e.totalPriceCents
        );
    }
}
