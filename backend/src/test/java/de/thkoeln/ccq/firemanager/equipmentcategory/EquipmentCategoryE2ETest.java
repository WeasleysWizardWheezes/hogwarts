package de.thkoeln.ccq.firemanager.equipmentcategory;

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
class EquipmentCategoryE2ETest {

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

    private String createCategoryViaApi(String name, String description) {
        String json = """
                {"name": "%s", "description": "%s"}
                """.formatted(name, description);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/equipment-categories"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    @Test
    void createAndGetCategory_returnsCreatedCategory() {
        // Arrange & Act
        String json = """
                {"name": "Atemschutz", "description": "Atemschutzgeräte und Zubehör"}
                """;
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                url("/api/v1/equipment-categories"), jsonEntity(json), String.class);

        // Assert creation
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String id = extractId(createResponse.getBody());

        // Act - fetch by ID
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/api/v1/equipment-categories/" + id), String.class);

        // Assert
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("Atemschutz");
        assertThat(getResponse.getBody()).contains("Atemschutzgeräte und Zubehör");
    }

    @Test
    void deleteCategory_archivesAndRemovesFromList() {
        // Arrange
        String id = createCategoryViaApi("Funk", "Funkgeräte");

        // Act - delete (archive)
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/api/v1/equipment-categories/" + id),
                HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify it's gone from list
        ResponseEntity<String> listResponse = restTemplate.getForEntity(
                url("/api/v1/equipment-categories?page=0&size=20"), String.class);
        assertThat(listResponse.getBody()).doesNotContain("Funk");
    }
}
