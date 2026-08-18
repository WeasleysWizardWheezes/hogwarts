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
        String json = """
                {"name": "%s", "description": "Testkategorie"}
                """.formatted(name);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/equipment-categories"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    private String createEquipmentViaApi(String name, String inventoryNumber, String categoryId) {
        String json = """
                {
                    "name": "%s",
                    "inventoryNumber": "%s",
                    "categoryId": "%s"
                }
                """.formatted(name, inventoryNumber, categoryId);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/equipment"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    @Test
    void createEquipment_returnsCreated() {
        // Arrange
        String categoryId = createCategoryViaApi("Atemschutz");

        String json = """
                {
                    "name": "Atemschutzgerät PA 300",
                    "inventoryNumber": "AT-2024-001",
                    "description": "Pressluftatemschutzgerät",
                    "status": "VERFUEGBAR",
                    "categoryId": "%s"
                }
                """.formatted(categoryId);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/equipment"), jsonEntity(json), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString())
                .contains("/api/v1/equipment/");
        assertThat(response.getBody()).contains("Atemschutzgerät PA 300");
        assertThat(response.getBody()).contains("AT-2024-001");
        assertThat(response.getBody()).contains("VERFUEGBAR");
        assertThat(response.getBody()).contains(categoryId);
    }

    @Test
    void getEquipment_returnsCorrectDataAfterCreate() {
        // Arrange
        String categoryId = createCategoryViaApi("Atemschutz");
        String equipmentId = createEquipmentViaApi("Wärmebildkamera TIC 360", "WB-2024-001", categoryId);

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/equipment/" + equipmentId), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Wärmebildkamera TIC 360");
        assertThat(response.getBody()).contains("WB-2024-001");
        assertThat(response.getBody()).contains(categoryId);
    }

    @Test
    void deleteEquipment_returnsNoContent() {
        // Arrange
        String categoryId = createCategoryViaApi("Atemschutz");
        String equipmentId = createEquipmentViaApi("Hydraulikspreizer SP 500", "HS-2024-001", categoryId);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/v1/equipment/" + equipmentId),
                HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
