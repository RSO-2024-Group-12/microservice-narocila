package si.nakupify.endpoint.internal;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import si.nakupify.entity.OrderStatus;
import si.nakupify.service.OrderService;
import si.nakupify.dto.request.CreateOrderRequest;

@Path("/internal/orders")
@Transactional
public class InternalOrdersEndpoint {

    @Inject
    OrderService service;

    public record UpdateStatusRequest(OrderStatus status) {}

    @POST
    public Response create(@Valid CreateOrderRequest req) {
        return service.createResponse(req);
    }

    @PATCH
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, UpdateStatusRequest req) {
        if (req == null || req.status == null) {
            throw new BadRequestException("status is required");
        }
        return service.updateStatusResponse(id, req.status);
    }
}
