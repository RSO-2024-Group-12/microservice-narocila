package si.nakupify.dto.vo;

public record ShipmentCreatedVO(
        Long shipmentId,
        Long orderId,
        String trackingNumber,
        String status
) {}
