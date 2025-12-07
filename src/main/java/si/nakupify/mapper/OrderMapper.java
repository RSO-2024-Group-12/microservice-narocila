package si.nakupify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import si.nakupify.dto.OrderDTO;
import si.nakupify.entity.OrderEntity;
import si.nakupify.entity.OrderItemEntity;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderDTO toDto(OrderEntity entity, List<OrderItemEntity> items);
}
