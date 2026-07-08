package de.thkoeln.ccq.firemanager;

import de.thkoeln.ccq.firemanager.dto.AssignEquipmentRequest;
import de.thkoeln.ccq.firemanager.dto.CreateOperationRequest;
import de.thkoeln.ccq.firemanager.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
class OperationE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationRepository operationRepository;

    @BeforeEach
    void cleanDatabase() {
        operationRepository.deleteAll();
    }

    @Test
    void shouldCreateOperationAndReturn201() throws Exception {
        // Arrange
        var requestBody = """
                {
                    "title": "Einsatz Feuerwehr",
                    "operationType": "Brandbekämpfung",
                    "startTime": "2026-07-08T14:00:00",
                    "endTime": "2026-07-08T16:00:00",
                    "requiredEquipment": ["550e8400-e29b-41d4-a716-446655440000"],
                    "description": "Brand in Bürogebäude"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {
        // Arrange
        var requestBody = """
                {
                    "title": "",
                    "operationType": "Brandbekämpfung",
                    "startTime": "2026-07-08T14:00:00",
                    "endTime": "2026-07-08T16:00:00"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllOperationsWhenEmpty() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldGetAllOperationsWithCreatedOperations() throws Exception {
        // Arrange
        var createRequest = """
                {
                    "title": "Einsatz 1",
                    "operationType": "Brandbekämpfung",
                    "startTime": "2026-07-08T14:00:00",
                    "endTime": "2026-07-08T16:00:00",
                    "description": "Erster Einsatz"
                }
                """;
        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated());

        var createRequest2 = """
                {
                    "title": "Einsatz 2",
                    "operationType": "Hilfeleistung",
                    "startTime": "2026-07-08T18:00:00",
                    "endTime": "2026-07-08T20:00:00",
                    "description": "Zweiter Einsatz"
                }
                """;
        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest2))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/v1/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].title").value("Einsatz 1"))
                .andExpect(jsonPath("$[1].title").value("Einsatz 2"));
    }

    @Test
    void shouldAssignEquipmentToOperation() throws Exception {
        // Arrange
        var createRequest = """
                {
                    "title": "Einsatz Feuerwehr",
                    "operationType": "Brandbekämpfung",
                    "startTime": "2026-07-08T14:00:00",
                    "endTime": "2026-07-08T16:00:00",
                    "description": "Brand in Bürogebäude"
                }
                """;
        var response = mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn();

        var operationId = extractOperationId(response.getResponse().getContentAsString());

        var assignRequest = """
                {
                    "equipmentIds": ["550e8400-e29b-41d4-a716-446655440000"]
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/operations/{id}/assign", operationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Einsatz Feuerwehr"));
    }

    @Test
    void shouldGetAvailableEquipment() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/operations/available-equipment")
                        .param("operationType", "Brandbekämpfung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private UUID extractOperationId(String responseBody) {
        // Extract ID from JSON response
        // Format: {"id":"uuid","title":...}
        var start = responseBody.indexOf("\"id\":\"") + 6;
        var end = responseBody.indexOf("\"", start);
        return UUID.fromString(responseBody.substring(start, end));
    }
}