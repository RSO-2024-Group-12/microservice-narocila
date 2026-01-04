package si.nakupify.endpoint;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import si.nakupify.dto.OrderDto;
import si.nakupify.dto.OrderItemDto;
import si.nakupify.entity.OrderStatus;
import si.nakupify.service.OrderService;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestSecurity(user = "test-user")
public class UserOrdersEndpointTest {

    @InjectMock
    OrderService service;

    private static OrderDto sampleOrder(Long id, Long userId) {
        return new OrderDto(
                id, userId,
                "John Doe", "Main St", "1A", "Ljubljana", "1000", "SI",
                12345L, 500L, true, "CARD",
                55L, "TRK-123", OrderStatus.SHIPPED,
                Instant.now(), Instant.now(),
                List.of(new OrderItemDto(1L, 10L, 2, 1000L, 2000L))
        );
    }

    @Test
    void list_shouldReturnOrdersFromService() {
        when(service.list(0, 50, 42L)).thenReturn(List.of(sampleOrder(1L, 42L)));

        given()
                .accept(ContentType.JSON)
                .queryParam("userId", 42)
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", equalTo(1))
                .body("[0].userId", equalTo(42));

        verify(service).list(0, 50, 42L);
    }

    @Test
    void get_shouldReturnOrderFromService() {
        when(service.getByIdDto(5L)).thenReturn(sampleOrder(5L, 99L));

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/orders/5")
                .then()
                .statusCode(200)
                .body("id", equalTo(5))
                .body("trackingNumber", equalTo("TRK-123"));

        verify(service).getByIdDto(5L);
    }
}
