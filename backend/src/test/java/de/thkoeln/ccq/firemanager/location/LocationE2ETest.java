package de.thkoeln.ccq.firemanager.location;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LocationE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> jsonEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    @Test
    void createLocation_returnsCreated() {
        // Arrange
        String json = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(json), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Gerätehaus Köln");
        assertThat(response.getBody()).contains("Musterstraße 1, 50677 Köln");
        assertThat(response.getBody()).contains("FIRE_STATION");
    }

    @Test
    void createLocation_returnsBadRequestWhenNameIsBlank() {
        // Arrange
        String json = """
            {
                "name": "",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(json), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getLocation_returnsOkWhenLocationExists() {
        // Arrange
        String createJson = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(createJson), String.class);
        
        String locationId = extractIdFromResponse(createResponse.getBody());

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/locations/" + locationId), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Gerätehaus Köln");
    }

    @Test
    void getLocation_returnsNotFoundWhenLocationDoesNotExist() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/locations/" + nonExistentId), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listLocations_returnsEmptyListWhenNoLocationsExist() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/locations"), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("[]");
    }

    @Test
    void listLocations_returnsAllLocations() {
        // Arrange
        String json1 = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        String json2 = """
            {
                "name": "Gerätehaus Bonn",
                "address": "Beispielweg 2, 53111 Bonn",
                "type": "FIRE_STATION"
            }
            """;
        
        restTemplate.postForEntity(url("/api/v1/locations"), jsonEntity(json1), String.class);
        restTemplate.postForEntity(url("/api/v1/locations"), jsonEntity(json2), String.class);

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/locations"), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Gerätehaus Köln");
        assertThat(response.getBody()).contains("Gerätehaus Bonn");
    }

    @Test
    void updateLocation_returnsOk() {
        // Arrange
        String createJson = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(createJson), String.class);
        
        String locationId = extractIdFromResponse(createResponse.getBody());

        String updateJson = """
            {
                "name": "Gerätehaus Köln-Zentrum",
                "address": "Neue Straße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/locations/" + locationId),
                HttpMethod.PUT,
                jsonEntity(updateJson),
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Gerätehaus Köln-Zentrum");
        assertThat(response.getBody()).contains("Neue Straße 1, 50677 Köln");
    }

    @Test
    void updateLocation_returnsNotFoundWhenLocationDoesNotExist() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        String updateJson = """
            {
                "name": "Gerätehaus Köln-Zentrum",
                "address": "Neue Straße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/locations/" + nonExistentId),
                HttpMethod.PUT,
                jsonEntity(updateJson),
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteLocation_returnsNoContent() {
        // Arrange
        String createJson = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(createJson), String.class);
        
        String locationId = extractIdFromResponse(createResponse.getBody());

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/locations/" + locationId),
                HttpMethod.DELETE,
                null,
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteLocation_returnsNotFoundWhenLocationDoesNotExist() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/locations/" + nonExistentId),
                HttpMethod.DELETE,
                null,
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private String extractIdFromResponse(String responseBody) {
        // Simple extraction of UUID from JSON response
        int start = responseBody.indexOf("\"id\":");
        if (start == -1) {
            throw new IllegalStateException("Cannot extract ID from response: " + responseBody);
        }
        start += 6; // Skip "id":"
        int end = responseBody.indexOf("\"", start);
        return responseBody.substring(start, end);
    }
}