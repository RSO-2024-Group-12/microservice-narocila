package si.nakupify.endpoint.external;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import si.nakupify.dto.OrderDTO;
import si.nakupify.service.OrderService;

import java.util.List;

@Path("/api/orders")
public class UserOrdersEndpoint {

    @Inject
    OrderService service;


    @GET
    public List<OrderDTO> list(@QueryParam("page") @DefaultValue("0") int page,
                               @QueryParam("size") @DefaultValue("50") int size,
                               @QueryParam("userId") Long userId) {
        return service.listDtos(page, size, userId);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return service.getByIdResponse(id);
    }
}
