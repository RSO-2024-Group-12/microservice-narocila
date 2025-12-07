package si.nakupify.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import si.nakupify.dto.OrderDto;
import si.nakupify.entity.OrderStatus;
import si.nakupify.service.OrderService;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class InternalOrdersEndpointTest {

    @InjectMock
    OrderService service;

    private static OrderDto dto(Long id) {
        return new OrderDto(id, 1L, "John", "St", "1", "City", "1000", "SI", 100L, 10L, true, "CARD", 11L, "TRK", OrderStatus.CREATED, Instant.now(), Instant.now(), List.of());
    }

    @Test
    void create_returnsDto() {
        when(service.createReturningDto(any())).thenReturn(dto(10L));

        String body = "{" +
                "\"userId\":1,\"recipientName\":\"John\",\"street\":\"St\",\"houseNumber\":\"1\",\"city\":\"City\",\"postalCode\":\"1000\",\"country\":\"SI\",\"paymentMethod\":\"CARD\",\"paid\":true,\"shippingCostCents\":10,\"items\":[{\"productId\":5,\"quantity\":1,\"unitPriceCents\":90}]}";

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/internal/orders")
        .then()
            .statusCode(200)
            .body("id", equalTo(10));

        verify(service).createReturningDto(any());
    }

    @Test
    void updateStatus_returnsDto() {
        when(service.updateStatusDto(22L, OrderStatus.SHIPPED)).thenReturn(dto(22L));

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "SHIPPED"))
        .when()
            .patch("/internal/orders/22/status")
        .then()
            .statusCode(200)
            .body("id", equalTo(22));

        verify(service).updateStatusDto(22L, OrderStatus.SHIPPED);
    }
}
