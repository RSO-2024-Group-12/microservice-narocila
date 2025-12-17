package si.nakupify.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import si.nakupify.dto.OrderDto;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import si.nakupify.entity.OrderEntity;
import si.nakupify.entity.OrderItemEntity;
import si.nakupify.entity.OrderStatus;
import si.nakupify.mapper.OrderMapper;
import si.nakupify.repository.OrderItemRepository;
import si.nakupify.repository.OrderRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class OrderServiceTest {

    @Inject
    OrderService service;

    @InjectMock
    OrderRepository orderRepository;

    @InjectMock
    OrderItemRepository itemRepository;

    @InjectMock
    OrdersMessagingService messaging;

    @InjectMock
    OrderMapper mapper;

    private static OrderEntity orderEntity(Long id) {
        OrderEntity e = new OrderEntity();
        e.id = id;
        e.userId = 7L;
        e.status = OrderStatus.CREATED;
        e.createdAt = Instant.now();
        e.updatedAt = e.createdAt;
        return e;
    }

    @Test
    void getByIdDto_returnsMappedDto() {
        var entity = orderEntity(1L);
        when(orderRepository.findByIdOrThrow(1L)).thenReturn(entity);
        @SuppressWarnings("unchecked")
        PanacheQuery<OrderItemEntity> q = mock(PanacheQuery.class);
        when(q.list()).thenReturn(List.of());
        when(itemRepository.find("order", entity)).thenReturn(q);

        OrderDto dto = new OrderDto(1L, 7L, null,null,null,null,null,null,null,null,false,null,null,null,OrderStatus.CREATED, Instant.now(), Instant.now(), List.of());
        when(mapper.toDto(eq(entity), anyList())).thenReturn(dto);

        var result = service.getByIdDto(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(orderRepository).findByIdOrThrow(1L);
        verify(mapper).toDto(eq(entity), anyList());
    }

    @Test
    void updateStatusDto_updatesAndReturnsDto() {
        var entity = orderEntity(2L);
        when(orderRepository.findByIdOrThrow(2L)).thenReturn(entity);
        @SuppressWarnings("unchecked")
        PanacheQuery<OrderItemEntity> q = mock(PanacheQuery.class);
        when(q.list()).thenReturn(List.of());
        when(itemRepository.find("order", entity)).thenReturn(q);

        OrderDto dto = new OrderDto(2L, 7L, null,null,null,null,null,null,null,null,false,null,null,null,OrderStatus.SHIPPED, Instant.now(), Instant.now(), List.of());
        when(mapper.toDto(eq(entity), anyList())).thenReturn(dto);

        var result = service.updateStatusDto(2L, OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, result.status());
        verify(orderRepository).findByIdOrThrow(2L);
    }
}
