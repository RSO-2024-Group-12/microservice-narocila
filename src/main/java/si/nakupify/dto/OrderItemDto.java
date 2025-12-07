package si.nakupify.dto;

public record OrderItemDto(
        Long id,
        Long productId,
        Integer quantity,
        Long unitPriceCents,
        Long totalPriceCents
) {
}
