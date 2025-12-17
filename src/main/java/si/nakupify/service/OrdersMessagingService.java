package si.nakupify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import si.nakupify.dto.vo.ShipmentCreatedVO;
import si.nakupify.dto.vo.ShipmentRequestedVO;
import si.nakupify.entity.OrderEntity;
import si.nakupify.repository.OrderRepository;

/**
 * Kafka producer/consumer for microservice-narocila.
 * Produces order events and consumes messages from microservice-nakup.
 */
@ApplicationScoped
public class OrdersMessagingService {

    @Inject
    @Channel("orders-out")
    Emitter<String> ordersEmitter;

    @Inject
    @Channel("shipment-requests-out")
    Emitter<String> shipmentRequestsEmitter;

    @Inject
    ObjectMapper mapper;

    @Inject
    OrderRepository orderRepository;

    public void emitShipmentRequested(OrderEntity order) {
        try {
            var vo = new ShipmentRequestedVO(
                    order.id,
                    "LOCAL",
                    order.shippingCostCents == null ? 0L : order.shippingCostCents,
                    order.recipientName,
                    order.street,
                    order.houseNumber,
                    order.city,
                    order.postalCode,
                    order.country
            );
            shipmentRequestsEmitter.send(mapper.writeValueAsString(vo));
        } catch (Exception ignored) {
        }
    }

    @Incoming("nakup-in")
    public void onPurchaseMessage(String message) {
        System.out.println("[narocila] Received from nakup: " + message);
    }

    @Incoming("shipments-in")
    public void onShipmentCreated(String message) {
        try {
            var vo = mapper.readValue(message, ShipmentCreatedVO.class);
            if (vo.orderId() != null) {
                addTrackingInfo(vo);
            }
        } catch (Exception e) {
            System.err.println("[narocila] Failed to process SHIPMENT_CREATED: " + e.getMessage());
        }
    }

    @Transactional
    protected void addTrackingInfo(ShipmentCreatedVO vo) {
        var order = orderRepository.findById(vo.orderId());
        if (order != null) {
            order.shipmentId = vo.shipmentId();
            order.trackingNumber = vo.trackingNumber();
            orderRepository.persist(order);
            System.out.println("[narocila] Updated order " + vo.orderId() + " with shipment " + vo.shipmentId());
        }
    }
}
