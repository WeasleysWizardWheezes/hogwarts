package de.thkoeln.ccq.firemanager.equipment;

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
class EquipmentE2ETest {

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

    private String createCategoryViaApi(String name) {
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/equipment-categories"),
                jsonEntity("{\"name\":\"%s\"}".formatted(name)),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    private String createVehicleGroupViaApi(String name) {
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicle-groups"),
                jsonEntity("{\"name\":\"%s\"}".formatted(name)),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    private String createVehicleViaApi(String vehicleGroupId) {
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
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/vehicles"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    private String createEquipmentViaApi(String categoryId, String vehicleId, String inventoryNumber) {
        String json = """
                {
                    "name": "Pressluftatmer PA 300",
                    "inventoryNumber": "%s",
                    "description": "300 bar",
                    "categoryId": "%s",
                    "vehicleId": "%s",
                    "nextInspectionDate": "2026-06-15",
                    "nextMaintenanceDate": "2026-09-01"
                }
                """.formatted(inventoryNumber, categoryId, vehicleId);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/equipment"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    @Test
    void createEquipment_returnsCreatedAndCanBeRead() {
        // Arrange
        String categoryId = createCategoryViaApi("Atemschutz");
        String vehicleGroupId = createVehicleGroupViaApi("Löschfahrzeuge");
        String vehicleId = createVehicleViaApi(vehicleGroupId);
        String json = """
                {
                    "name": "Pressluftatmer PA 300",
                    "inventoryNumber": "AGT-2024-0042",
                    "categoryId": "%s",
                    "vehicleId": "%s"
                }
                """.formatted(categoryId, vehicleId);

        // Act
        ResponseEntity<String> created = restTemplate.postForEntity(
                url("/api/v1/equipment"), jsonEntity(json), String.class);
        String equipmentId = extractId(created.getBody());
        ResponseEntity<String> fetched = restTemplate.getForEntity(
                url("/api/v1/equipment/" + equipmentId), String.class);

        // Assert
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getHeaders().getLocation()).hasPath("/api/v1/equipment/" + equipmentId);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody()).contains("Pressluftatmer PA 300", categoryId, vehicleId);
    }

    @Test
    void updateEquipment_changesStatusAndPersistsHistory() {
        // Arrange
        String categoryId = createCategoryViaApi("Atemschutz");
        String vehicleGroupId = createVehicleGroupViaApi("Löschfahrzeuge");
        String vehicleId = createVehicleViaApi(vehicleGroupId);
        String equipmentId = createEquipmentViaApi(categoryId, vehicleId, "AGT-2024-0042");

        // Act
        ResponseEntity<String> updated = restTemplate.exchange(
                url("/api/v1/equipment/" + equipmentId),
                HttpMethod.PUT,
                jsonEntity("{\"status\":\"WARTUNG\"}"),
                String.class);
        ResponseEntity<String> history = restTemplate.getForEntity(
                url("/api/v1/equipment/" + equipmentId + "/history"), String.class);

        // Assert
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody()).contains("WARTUNG");
        assertThat(history.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(history.getBody()).contains("VERFUEGBAR", "WARTUNG");
    }

    @Test
    void listEquipment_returnsFilteredResultsAndDeleteReturnsNoContent() {
        // Arrange
        String categoryId = createCategoryViaApi("Atemschutz");
        String vehicleGroupId = createVehicleGroupViaApi("Löschfahrzeuge");
        String vehicleId = createVehicleViaApi(vehicleGroupId);
        createEquipmentViaApi(categoryId, vehicleId, "AGT-2024-0042");

        // Act
        ResponseEntity<String> list = restTemplate.getForEntity(
                url("/api/v1/equipment?page=0&size=20&search=AGT-2024"), String.class);
        String equipmentId = extractId(restTemplate.postForEntity(
                url("/api/v1/equipment"),
                jsonEntity("{\"name\":\"Funkgerät\",\"inventoryNumber\":\"F-001\",\"categoryId\":\"%s\"}"
                        .formatted(categoryId)),
                String.class).getBody());
        ResponseEntity<Void> deleted = restTemplate.exchange(
                url("/api/v1/equipment/" + equipmentId), HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(list.getBody()).contains("AGT-2024-0042");
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}