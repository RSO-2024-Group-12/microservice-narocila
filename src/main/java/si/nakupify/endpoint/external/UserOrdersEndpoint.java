package si.nakupify.endpoint.external;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import si.nakupify.dto.OrderDto;
import si.nakupify.service.OrderService;

import java.util.List;

@Path("/api/orders")
@Transactional
@Authenticated
public class UserOrdersEndpoint {
    @Inject
    OrderService service;

    @GET
    public List<OrderDto> list(@QueryParam("page") @DefaultValue("0") int page,
                               @QueryParam("size") @DefaultValue("50") int size,
                               @QueryParam("userId") @NotNull Long userId) {
        return service.list(page, size, userId);
    }

    @GET
    @Path("/{id}")
    public OrderDto get(@PathParam("id") Long id) {
        return service.getByIdDto(id);
    }
}
