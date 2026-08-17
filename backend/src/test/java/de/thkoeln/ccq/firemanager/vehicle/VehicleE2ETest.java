package de.thkoeln.ccq.firemanager.vehicle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> jsonEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    private String extractId(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode idNode = root.get("id");
            if (idNode != null && idNode.isTextual()) {
                return idNode.asText();
            }
            throw new AssertionError("No top-level 'id' field found in response: " + responseBody);
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError("Could not parse response JSON: " + responseBody, e);
        }
    }

    private String createVehicleGroupViaApi(String name, String beschreibung) {
        String json = """
                {"name": "%s", "beschreibung": "%s"}
                """.formatted(name, beschreibung);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicle-groups"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    private String createVehicleViaApi(String name, String funkrufname, String kennzeichen,
                                       int baujahr, String beschreibung, String status, String vehicleGroupId) {
        String json = """
                {
                    "name": "%s",
                    "funkrufname": "%s",
                    "kennzeichen": "%s",
                    "baujahr": %d,
                    "beschreibung": "%s",
                    "status": "%s",
                    "vehicleGroupId": "%s"
                }
                """.formatted(name, funkrufname, kennzeichen, baujahr, beschreibung, status, vehicleGroupId);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicles"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    @Test
    void createVehicle_returnsCreated() {
        // Arrange
        String vehicleGroupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");

        String json = """
                {
                    "name": "LF 10/6",
                    "funkrufname": "Florian Köln 1-46-1",
                    "kennzeichen": "K-AB 1234",
                    "baujahr": 2020,
                    "beschreibung": "Löschgruppenfahrzeug",
                    "status": "VERFUEGBAR",
                    "vehicleGroupId": "%s"
                }
                """.formatted(vehicleGroupId);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicles"), jsonEntity(json), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("LF 10/6");
        assertThat(response.getBody()).contains("Florian Köln 1-46-1");
        assertThat(response.getBody()).contains("K-AB 1234");
    }

    @Test
    void getVehicle_returnsCorrectData() {
        // Arrange
        String vehicleGroupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        String vehicleId = createVehicleViaApi("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", "VERFUEGBAR", vehicleGroupId);

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/vehicles/" + vehicleId), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("LF 10/6");
        assertThat(response.getBody()).contains(vehicleGroupId);
    }

    @Test
    void updateVehicle_returnsUpdatedData() {
        // Arrange
        String vehicleGroupId = createVehicleGroupViaApi("Löschfahrzeuge", "Beschreibung");
        String vehicleId = createVehicleViaApi("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", "VERFUEGBAR", vehicleGroupId);

        String updateJson = """
                {
                    "name": "LF 20/16",
                    "funkrufname": "Florian Köln 1-46-2",
                    "kennzeichen": "K-CD 5678",
                    "baujahr": 2022,
                    "beschreibung": "Neues Fahrzeug",
                    "status": "WARTUNG"
                }
                """;

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/vehicles/" + vehicleId),
                HttpMethod.PUT, jsonEntity(updateJson), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("LF 20/16");
        assertThat(response.getBody()).contains("WARTUNG");
    }
}
