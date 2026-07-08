package de.thkoeln.ccq.firemanager;

import de.thkoeln.ccq.firemanager.dto.AssignEquipmentRequest;
import de.thkoeln.ccq.firemanager.dto.CreateOperationRequest;
import de.thkoeln.ccq.firemanager.dto.EquipmentSummary;
import de.thkoeln.ccq.firemanager.dto.OperationResponse;
import de.thkoeln.ccq.firemanager.service.OperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationController.class)
class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OperationService operationServiceStub;

    private CreateOperationRequest validRequest;
    private OperationResponse validResponse;

    @BeforeEach
    void setup() {
        var operationType = "Brandbekämpfung";
        var startTime = LocalDateTime.now().plusHours(1);
        var endTime = LocalDateTime.now().plusHours(3);
        var requiredEquipment = List.of(UUID.randomUUID());

        validRequest = new CreateOperationRequest(
            "Einsatz Feuerwehr",
            operationType,
            startTime,
            endTime,
            requiredEquipment,
            "Brand in Bürogebäude"
        );

        validResponse = new OperationResponse(
            UUID.randomUUID(),
            "Einsatz Feuerwehr",
            operationType,
            startTime,
            endTime,
            requiredEquipment,
            "Brand in Bürogebäude",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
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

        when(operationServiceStub.createOperation(any(CreateOperationRequest.class))).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Einsatz Feuerwehr"))
                .andExpect(jsonPath("$.operationType").value("Brandbekämpfung"));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
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
    void shouldReturn400WhenOperationTypeIsBlank() throws Exception {
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
    void shouldGetOperationById() throws Exception {
        // Arrange
        var operationId = UUID.randomUUID();
        when(operationServiceStub.getOperationById(operationId)).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/operations/{id}", operationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(operationId.toString()))
                .andExpect(jsonPath("$.title").value("Einsatz Feuerwehr"));
    }

    @Test
    void shouldGetAllOperations() throws Exception {
        // Arrange
        var operations = List.of(validResponse);
        when(operationServiceStub.getAllOperations()).thenReturn(operations);

        // Act & Assert
        mockMvc.perform(get("/api/v1/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Einsatz Feuerwehr"));
    }

    @Test
    void shouldAssignEquipmentToOperation() throws Exception {
        // Arrange
        var operationId = UUID.randomUUID();
        var equipmentRequest = new AssignEquipmentRequest(List.of(UUID.randomUUID()));
        when(operationServiceStub.assignEquipment(operationId, equipmentRequest)).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/operations/{id}/assign", operationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "equipmentIds": ["550e8400-e29b-41d4-a716-446655440000"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Einsatz Feuerwehr"));
    }

    @Test
    void shouldReturnAvailableEquipment() throws Exception {
        // Arrange
        var operationType = "Brandbekämpfung";
        var equipment = List.of(
            new EquipmentSummary(UUID.randomUUID(), "LF 10/6", "Fahrzeug", "SN-001"),
            new EquipmentSummary(UUID.randomUUID(), "Atemschutzgerät", "Gerät", "SN-002")
        );
        when(operationServiceStub.getAvailableEquipment(operationType)).thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(get("/api/v1/operations/available-equipment")
                        .param("operationType", operationType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("LF 10/6"))
                .andExpect(jsonPath("$[1].name").value("Atemschutzgerät"));
    }
}