package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import si.nakupify.entity.OrderEntity;
import si.nakupify.entity.OrderItemEntity;
import si.nakupify.entity.OrderStatus;
import si.nakupify.repository.OrderItemRepository;
import si.nakupify.repository.OrderRepository;
import si.nakupify.dto.OrderDto;
import si.nakupify.mapper.OrderMapper;
import si.nakupify.dto.request.OrderRequestDto;
import si.nakupify.dto.request.OrderItemRequestDto;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class OrderService {
    @Inject
    OrderRepository orderRepository;
    @Inject
    OrderItemRepository itemRepository;
    @Inject
    OrdersMessagingService messaging;
    @Inject
    OrderMapper mapper;

    public List<OrderDto> list(int page, int size, Long userId) {
        if (size < 1) size = 50;
        return orderRepository.find("userId", userId).page(page, size).list().stream()
                .map(o -> mapper.toDto(o, itemsFor(o)))
                .toList();
    }

    public OrderEntity updateStatus(Long id, OrderStatus newStatus) throws NotFoundException {
        final var o = orderRepository.findByIdOrThrow(id);
        o.status = newStatus;
        o.updatedAt = Instant.now();
        return o;
    }

    public OrderDto getByIdDto(Long id) throws NotFoundException {
        var o = orderRepository.findByIdOrThrow(id);
        return mapper.toDto(o, itemsFor(o));
    }

    public OrderDto create(OrderRequestDto req) {
        final var o = new OrderEntity();
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
            for (OrderItemRequestDto ci : req.items()) {
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

        messaging.emitShipmentRequested(o);

        o.updatedAt = Instant.now();
        return mapper.toDto(o, itemsFor(o));
    }

    public OrderDto updateStatusDto(Long id, OrderStatus newStatus) throws NotFoundException {
        var updated = updateStatus(id, newStatus);
        return mapper.toDto(updated, itemsFor(updated));
    }

    private List<OrderItemEntity> itemsFor(OrderEntity o) {
        return itemRepository.find("order", o).list();
    }
}
