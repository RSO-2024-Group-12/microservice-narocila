package si.nakupify.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import si.nakupify.entity.OrderEntity;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<OrderEntity, Long> {
}
