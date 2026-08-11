package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
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
class VehicleE2ETest {

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

    private String createVehicleViaApi(String name, String radioCallName, String licensePlate,
                                       int year, String description, String status, String vehicleGroupId) {
        String json = """
                {"name": "%s", "radioCallName": "%s", "licensePlate": "%s",
                 "yearOfConstruction": %d, "description": "%s", "status": "%s",
                 "vehicleGroupId": "%s"}
                """.formatted(name, radioCallName, licensePlate, year, description, status, vehicleGroupId);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicles"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    @Test
    void createVehicle_returnsCreated() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");

        // Act
        String json = """
                {"name": "LF 10/6", "radioCallName": "Funk-01", "licensePlate": "M-AB1234",
                 "yearOfConstruction": 2020, "description": "Desc", "status": "VERFUEGBAR",
                 "vehicleGroupId": "%s"}
                """.formatted(groupId);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicles"), jsonEntity(json), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("\"name\":\"LF 10/6\"");
    }

    @Test
    void getAllVehicles_returnsListAfterCreate() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        createVehicleViaApi("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR.toString(), groupId);
        createVehicleViaApi("LF 20/16", "Funk-02", "M-CD5678", 2018, "Desc",
                Vehicle.VehicleStatus.WARTUNG.toString(), groupId);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url("/api/v1/vehicles"),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getVehicleById_returnsCorrectData() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        String vehicleId = createVehicleViaApi("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR.toString(), groupId);

        // Act
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url("/api/v1/vehicles/" + vehicleId),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("name")).isEqualTo("LF 10/6");
    }

    @Test
    void deleteVehicle_returnsNoContent() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        String vehicleId = createVehicleViaApi("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR.toString(), groupId);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/v1/vehicles/" + vehicleId),
                HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void updateVehicle_returnsUpdatedData() {
        // Arrange
        String groupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        String vehicleId = createVehicleViaApi("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR.toString(), groupId);
        String updateJson = """
                {"name": "LF 10/6 (aktualisiert)", "radioCallName": "Funk-01-updated", "licensePlate": "M-AB1234",
                 "yearOfConstruction": 2020, "description": "Neue Desc", "status": "WARTUNG",
                 "vehicleGroupId": "%s"}
                """.formatted(groupId);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/vehicles/" + vehicleId),
                HttpMethod.PUT, jsonEntity(updateJson), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("LF 10/6 (aktualisiert)");
    }
}