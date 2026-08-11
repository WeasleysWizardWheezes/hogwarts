package de.thkoeln.ccq.firemanager.vehiclegroup;

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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class VehicleGroupE2ETest {

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

    private String extractId(String responseBody) {
        // Sucht das erste "id":"..." Paar im JSON
        var pattern = java.util.regex.Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
        var matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new AssertionError("Could not extract id from response: " + responseBody);
    }

    private String createVehicleGroupViaApi(String name, String description) {
        String json = """
                {"name": "%s", "description": "%s"}
                """.formatted(name, description);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicle-groups"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    @Test
    void createVehicleGroup_returnsCreated() {
        // Act
        String json = """
                {"name": "Löschfahrzeuge", "description": "Beschreibung"}
                """;
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicle-groups"), jsonEntity(json), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("\"name\":\"Löschfahrzeuge\"");
    }

    @Test
    void getAllVehicleGroups_returnsListAfterCreate() {
        // Arrange
        createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        createVehicleGroupViaApi("Rettungsdienst", "Beschreibung");

        // Act
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url("/api/v1/vehicle-groups"),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getVehicleGroupById_returnsCorrectData() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");

        // Act
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url("/api/v1/vehicle-groups/" + groupId),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("name")).isEqualTo("Löschfahrzeuge");
    }

    @Test
    void deleteVehicleGroup_returnsNoContent() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/v1/vehicle-groups/" + groupId),
                HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void updateVehicleGroup_returnsUpdatedData() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        String updateJson = """
                {"name": "Löschfahrzeuge (aktualisiert)", "description": "Neue Beschreibung"}
                """;

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/vehicle-groups/" + groupId),
                HttpMethod.PUT, jsonEntity(updateJson), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Löschfahrzeuge (aktualisiert)");
    }
}