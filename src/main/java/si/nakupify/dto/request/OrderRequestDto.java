package si.nakupify.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record OrderRequestDto(
        @NotNull Long userId,
        @NotBlank String recipientName,
        @NotBlank String street,
        @NotBlank String houseNumber,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country,
        @NotBlank String paymentMethod,
        @NotNull Boolean paid,
        @Min(0) @NotNull Long shippingCostCents,
        @Valid @NotNull @Size(min = 1) List<OrderItemRequestDto> items
) {}
