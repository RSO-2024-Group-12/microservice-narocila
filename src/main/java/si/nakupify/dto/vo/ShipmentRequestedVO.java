package si.nakupify.dto.vo;

public record ShipmentRequestedVO(
        Long orderId,
        String carrier,
        Long shippingCostCents,
        String recipientName,
        String street,
        String houseNumber,
        String city,
        String postalCode,
        String country
) {}
