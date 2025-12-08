package si.nakupify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import si.nakupify.dto.OrderDto;
import si.nakupify.entity.OrderEntity;
import si.nakupify.entity.OrderItemEntity;

import java.util.List;

@Mapper(componentModel = "jakarta", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderDto toDto(OrderEntity entity, List<OrderItemEntity> items);
}
