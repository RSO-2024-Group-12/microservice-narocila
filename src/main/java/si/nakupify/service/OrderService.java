package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.nakupify.entity.OrderEntity;
import si.nakupify.entity.OrderItemEntity;
import si.nakupify.entity.OrderStatus;
import si.nakupify.repository.OrderItemRepository;
import si.nakupify.repository.OrderRepository;
import si.nakupify.dto.OrderDTO;
import si.nakupify.mapper.OrderMapper;
import si.nakupify.dto.request.CreateOrderRequest;
import si.nakupify.dto.request.CreateOrderItemRequest;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;
    @Inject
    OrderItemRepository itemRepository;
    @Inject
    ShippingGatewayService shippingGateway;

    @Inject
    OrdersMessagingService messaging;

    @Inject
    OrderMapper mapper;

    public Optional<OrderEntity> findById(Long id) {
        return Optional.ofNullable(orderRepository.findById(id));
    }

    public List<OrderEntity> listAll(int page, int size, Long userId) {
        if (userId != null) {
            return orderRepository.find("userId", userId).page(page, size).list();
        }
        return orderRepository.findAll().page(page, size).list();
    }

    /** DTO-returning variant for endpoints */
    public List<OrderDTO> listDtos(int page, int size, Long userId) {
        if (size < 1) size = 50;
        return listAll(page, size, userId).stream()
                .map(o -> mapper.toDto(o, itemsFor(o)))
                .toList();
    }

    public List<OrderItemEntity> itemsFor(OrderEntity o) {
        return itemRepository.find("order", o).list();
    }

    // no internal create item record; use request DTO

    @Transactional
    public OrderEntity create(CreateOrderRequest req) {
        OrderEntity o = new OrderEntity();
        o.userId = req.userId();
        o.recipientName = req.recipientName();
        o.street = req.street();
        o.houseNumber = req.houseNumber();
        o.city = req.city();
        o.postalCode = req.postalCode();
        o.country = req.country();
        o.paymentMethod = req.paymentMethod();
        o.paid = req.paid() != null ? req.paid() : Boolean.FALSE;
        o.shippingCostCents = req.shippingCostCents();
        o.status = o.paid ? OrderStatus.PAID : OrderStatus.CREATED;
        o.createdAt = Instant.now();
        o.updatedAt = o.createdAt;
        orderRepository.persist(o);

        long itemsTotal = 0;
        if (req.items() != null) {
            for (CreateOrderItemRequest ci : req.items()) {
                OrderItemEntity item = new OrderItemEntity();
                item.order = o;
                item.productId = ci.productId();
                item.quantity = ci.quantity();
                item.unitPriceCents = ci.unitPriceCents();
                item.totalPriceCents = ci.unitPriceCents() * Math.max(1, ci.quantity());
                itemsTotal += item.totalPriceCents;
                itemRepository.persist(item);
            }
        }
        o.totalPriceCents = itemsTotal + (o.shippingCostCents == null ? 0 : o.shippingCostCents);

        // Create shipment via shipping microservice
        ShippingGatewayService.CreateShipmentRequest sreq = new ShippingGatewayService.CreateShipmentRequest(
                o.id,
                "LOCAL",
                o.shippingCostCents,
                o.recipientName,
                o.street,
                o.houseNumber,
                o.city,
                o.postalCode,
                o.country
        );
        var shipment = shippingGateway.createShipment(sreq);
        if (shipment != null) {
            o.shipmentId = shipment.id();
            o.trackingNumber = shipment.trackingNumber();
            o.status = OrderStatus.SHIPPED; // optional: mark as shipped when label created
        }
        o.updatedAt = Instant.now();
        // Emit order created event
        if (messaging != null) messaging.emitOrderCreated(o);
        return o;
    }

    @Transactional
    public Optional<OrderEntity> updateStatus(Long id, OrderStatus newStatus) {
        OrderEntity o = orderRepository.findById(id);
        if (o == null) return Optional.empty();
        o.status = newStatus;
        o.updatedAt = Instant.now();
        if (messaging != null) messaging.emitOrderUpdated(o);
        return Optional.of(o);
    }

    // Convenience methods that return ready DTOs/Responses for endpoints
    public Response getByIdResponse(Long id) {
        var opt = findById(id);
        return opt.<Response>map(o -> Response.ok(mapper.toDto(o, itemsFor(o))).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    public OrderDTO createReturningDto(CreateOrderRequest req) {
        var created = create(req);
        return mapper.toDto(created, itemsFor(created));
    }

    public Response updateStatusResponse(Long id, OrderStatus newStatus) {
        var updated = updateStatus(id, newStatus);
        return updated.<Response>map(o -> Response.ok(mapper.toDto(o, itemsFor(o))).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    public Response createResponse(CreateOrderRequest req) {
        var created = create(req);
        var dto = mapper.toDto(created, itemsFor(created));
        return Response.created(java.net.URI.create("/internal/orders/" + created.id)).entity(dto).build();
    }
}
