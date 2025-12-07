package si.nakupify.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import si.nakupify.entity.OrderEntity;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<OrderEntity, Long> {
    public OrderEntity findByIdOrThrow(Long id) throws NotFoundException {
        OrderEntity entity = findById(id);
        if (entity == null) {
            throw new NotFoundException("Order not found: id=" + id);
        }
        return entity;
    }
}
