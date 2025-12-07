package si.nakupify.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import si.nakupify.entity.OrderItemEntity;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepositoryBase<OrderItemEntity, Long> {
}
