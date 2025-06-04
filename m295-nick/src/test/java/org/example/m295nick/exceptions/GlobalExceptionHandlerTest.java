package org.example.m295nick.exceptions;

import org.example.m295nick.M295NickApplication;
import org.example.m295nick.models.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(
        classes = M295NickApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class GlobalExceptionHandlerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate; // Du kannst es auch über new RestTemplate() instanziieren, aber per @Autowired nutzt Spring die vorhandene Konfiguration

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

        try {
            restTemplate.exchange(baseUrl(), HttpMethod.POST, request, String.class);
            fail("Erwartet wurde eine HttpClientErrorException wegen Status 400");
        } catch (HttpClientErrorException ex) {
            // 400 Bad Request wegen Validierungsfehlern
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            String body = ex.getResponseBodyAsString();
            // Die JSON‐Antwort sollte im "errors"-Objekt Einträge für "brand" und "seats" enthalten
            assertThat(body).contains("\"brand\"");
            assertThat(body).contains("\"seats\"");
        }
    }

    @Test
    @DisplayName("GET /api/v1/vehicles/{id} für nicht vorhandene ID liefert 404")
    void whenGetNonExisting_thenNotFound() {
        try {
            restTemplate.getForEntity(baseUrl() + "/9999", String.class);
            fail("Erwartet wurde eine HttpClientErrorException wegen Status 404");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            String body = ex.getResponseBodyAsString();
            // Die Exception-Response sollte die Nachricht aus ResourceNotFoundException enthalten
            assertThat(body).contains("Vehicle wurde nicht gefunden");
        }
    }
}
