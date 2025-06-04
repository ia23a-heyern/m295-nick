package org.example.m295nick.exceptions;

import org.example.m295nick.M295NickApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = M295NickApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.security.user.name=test",
                "spring.security.user.password=test"
        }
)
class GlobalExceptionHandlerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/vehicles";
    }

    @Test
    @DisplayName("POST /api/v1/vehicles mit ungültigem Body liefert 400 und Validierungsfehler")
    void whenPostInvalidVehicle_thenValidationErrors() {
        String invalidJson = """
            {
              "model": "Test",
              "firstRegistration": "2025-12-12",
              "hasAirConditioning": true,
              "pricePerDay": -10,
              "seats": 0
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<Map> response = restTemplate
                .withBasicAuth("test", "test")
                .exchange(baseUrl(), HttpMethod.POST, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = response.getBody();
        assertThat(body).containsKey("errors");
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertThat(errors).containsKey("brand");
        assertThat(errors).containsKey("seats");
    }

    @Test
    @DisplayName("GET /api/v1/vehicles/{id} für nicht vorhandene ID liefert 404")
    void whenGetNonExisting_thenNotFound() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("test", "test")
                .getForEntity(baseUrl() + "/9999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        String body = response.getBody();
        assertThat(body).contains("Vehicle wurde nicht gefunden");
    }
}
