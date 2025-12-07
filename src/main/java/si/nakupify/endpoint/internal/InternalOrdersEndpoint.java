package si.nakupify.endpoint.internal;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import si.nakupify.entity.OrderStatus;
import si.nakupify.service.OrderService;
import si.nakupify.dto.OrderDto;
import si.nakupify.dto.request.OrderRequestDto;

@Path("/internal/orders")
@Transactional
public class InternalOrdersEndpoint {

    @Inject
    OrderService service;

    public record UpdateStatusRequest(OrderStatus status) {}

    @POST
    public OrderDto create(@Valid OrderRequestDto req) {
        return service.createReturningDto(req);
    }

    @PATCH
    @Path("/{id}/status")
    public OrderDto updateStatus(@PathParam("id") Long id, UpdateStatusRequest req) {
        if (req == null || req.status == null) {
            throw new BadRequestException("status is required");
        }
        return service.updateStatusDto(id, req.status);
    }
}
