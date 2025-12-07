package si.nakupify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import si.nakupify.entity.OrderEntity;

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
    ObjectMapper mapper;

    public void emitOrderCreated(OrderEntity order) {
        try {
            var node = mapper.createObjectNode();
            node.put("type", "ORDER_CREATED");
            node.put("orderId", order.id);
            node.put("userId", order.userId);
            node.put("status", order.status.name());
            node.put("totalPriceCents", order.totalPriceCents == null ? 0 : order.totalPriceCents);
            if (order.trackingNumber != null) node.put("trackingNumber", order.trackingNumber);
            ordersEmitter.send(mapper.writeValueAsString(node));
        } catch (Exception ignored) {
        }
    }

    public void emitOrderUpdated(OrderEntity order) {
        try {
            var node = mapper.createObjectNode();
            node.put("type", "ORDER_UPDATED");
            node.put("orderId", order.id);
            node.put("status", order.status.name());
            ordersEmitter.send(mapper.writeValueAsString(node));
        } catch (Exception ignored) {
        }
    }

    @Incoming("nakup-in")
    public void onPurchaseMessage(String message) {
        // For now, just log or ignore. Could react to events from purchase service.
        System.out.println("[narocila] Received from nakup: " + message);
    }
}
