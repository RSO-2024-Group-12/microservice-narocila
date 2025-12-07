package si.nakupify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Minimal HTTP client to call microservice-posiljanje internal API.
 * Avoids adding REST Client dependency for simplicity.
 */
@ApplicationScoped
public class ShippingGatewayService {

    @ConfigProperty(name = "shipments.base-url", defaultValue = "http://microservice-posiljanje:8080")
    String baseUrl;

    @Inject
    ObjectMapper mapper;

    private final HttpClient client = HttpClient.newHttpClient();

    public record CreateShipmentRequest(
            Long orderId,
            String carrier,
            Long shippingCostCents,
            String recipientName,
            String street,
            String houseNumber,
            String city,
            String postalCode,
            String country
    ) {}

    public record ShipmentResponse(Long id, Long orderId, String trackingNumber) {}

    public ShipmentResponse createShipment(CreateShipmentRequest req) {
        try {
            String body = mapper.writeValueAsString(req);
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/internal/shipments"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = client.send(httpReq, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                var tree = mapper.readTree(resp.body());
                Long id = tree.path("id").isNumber() ? tree.get("id").asLong() : null;
                Long orderId = tree.path("orderId").isNumber() ? tree.get("orderId").asLong() : null;
                String tracking = tree.path("trackingNumber").asText(null);
                return new ShipmentResponse(id, orderId, tracking);
            }
            return null;
        } catch (Exception e) {
            return null; // fail softly for now
        }
    }
}
