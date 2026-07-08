package de.thkoeln.ccq.firemanager;

import de.thkoeln.ccq.firemanager.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OperationE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OperationRepository operationRepository;

    @BeforeEach
    void cleanDatabase() {
        operationRepository.deleteAll();
    }

    @Test
    void shouldCreateOperationAndReturn201() {
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

        // Act
        var response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/operations",
            requestBody,
            OperationResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Einsatz Feuerwehr");
    }

    @Test
    void shouldReturn400WhenValidationFails() {
        // Arrange
        var requestBody = """
                {
                    "title": "",
                    "operationType": "Brandbekämpfung",
                    "startTime": "2026-07-08T14:00:00",
                    "endTime": "2026-07-08T16:00:00"
                }
                """;

        // Act
        var response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/operations",
            requestBody,
            String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldGetAllOperationsWhenEmpty() {
        // Act
        var response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/operations",
            OperationResponse[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldGetAllOperationsWithCreatedOperations() {
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
        restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/operations",
            createRequest,
            Object.class
        );

        var createRequest2 = """
                {
                    "title": "Einsatz 2",
                    "operationType": "Hilfeleistung",
                    "startTime": "2026-07-08T18:00:00",
                    "endTime": "2026-07-08T20:00:00",
                    "description": "Zweiter Einsatz"
                }
                """;
        restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/operations",
            createRequest2,
            Object.class
        );

        // Act
        var response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/operations",
            OperationResponse[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()[0].title()).isEqualTo("Einsatz 1");
        assertThat(response.getBody()[1].title()).isEqualTo("Einsatz 2");
    }

    @Test
    void shouldAssignEquipmentToOperation() {
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
        var response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/operations",
            createRequest,
            OperationResponse.class
        );
        var operationId = response.getBody().id();

        var assignRequest = """
                {
                    "equipmentIds": ["550e8400-e29b-41d4-a716-446655440000"]
                }
                """;
        var assignUrl = "http://localhost:" + port + "/api/v1/operations/" + operationId + "/assign";

        // Act
        var assignResponse = restTemplate.postForEntity(
            assignUrl,
            assignRequest,
            OperationResponse.class
        );

        // Assert
        assertThat(assignResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(assignResponse.getBody().title()).isEqualTo("Einsatz Feuerwehr");
    }

    @Test
    void shouldGetAvailableEquipment() {
        // Act
        var response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/operations/available-equipment?operationType=Brandbekämpfung",
            EquipmentSummary[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}