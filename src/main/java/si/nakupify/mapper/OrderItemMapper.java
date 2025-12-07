package si.nakupify.mapper;

import org.mapstruct.Mapper;
import si.nakupify.dto.OrderItemDTO;
import si.nakupify.entity.OrderItemEntity;

@Mapper(componentModel = "cdi")
public interface OrderItemMapper {
    OrderItemDTO toDto(OrderItemEntity entity);
}
