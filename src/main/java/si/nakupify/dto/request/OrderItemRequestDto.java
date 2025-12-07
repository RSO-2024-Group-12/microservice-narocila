package si.nakupify.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity,
        @NotNull @Min(0) Long unitPriceCents
) {}
