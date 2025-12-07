package si.nakupify.mapper;

import org.mapstruct.Mapper;
import si.nakupify.dto.OrderItemDto;
import si.nakupify.entity.OrderItemEntity;

@Mapper(componentModel = "cdi")
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItemEntity entity);
}
