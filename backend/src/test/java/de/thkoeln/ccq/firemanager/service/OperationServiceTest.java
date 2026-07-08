package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.AssignEquipmentRequest;
import de.thkoeln.ccq.firemanager.dto.CreateOperationRequest;
import de.thkoeln.ccq.firemanager.dto.OperationResponse;
import de.thkoeln.ccq.firemanager.model.Operation;
import de.thkoeln.ccq.firemanager.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @Mock
    private OperationRepository operationRepositoryStub;

    @InjectMocks
    private OperationService sut;

    private CreateOperationRequest validRequest;
    private OperationResponse expectedResponse;

    @BeforeEach
    void setup() {
        var operationType = "Brandbekämpfung";
        var startTime = LocalDateTime.now().plusHours(1);
        var endTime = LocalDateTime.now().plusHours(3);
        var requiredEquipment = List.of(UUID.randomUUID(), UUID.randomUUID());

        validRequest = new CreateOperationRequest(
            "Einsatz Feuerwehr",
            operationType,
            startTime,
            endTime,
            requiredEquipment,
            "Brand in Bürogebäude"
        );

        expectedResponse = new OperationResponse(
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
    void shouldCreateOperationSuccessfully() {
        // Arrange
        when(operationRepositoryStub.save(any())).thenReturn(mapToEntity(expectedResponse));

        // Act
        var result = sut.createOperation(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Einsatz Feuerwehr");
        assertThat(result.operationType()).isEqualTo("Brandbekämpfung");
        verify(operationRepositoryStub, times(1)).save(any());
    }

    @Test
    void shouldGetOperationByIdWhenExists() {
        // Arrange
        var operationId = UUID.randomUUID();
        when(operationRepositoryStub.findById(operationId)).thenReturn(Optional.of(mapToEntity(expectedResponse)));

        // Act
        var result = sut.getOperationById(operationId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(operationId);
        assertThat(result.title()).isEqualTo("Einsatz Feuerwehr");
    }

    @Test
    void shouldThrowWhenOperationNotFound() {
        // Arrange
        var operationId = UUID.randomUUID();
        when(operationRepositoryStub.findById(operationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getOperationById(operationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Operation not found");
    }

    @Test
    void shouldGetAllOperations() {
        // Arrange
        var operation1 = new OperationResponse(
            UUID.randomUUID(),
            "Einsatz 1",
            "Brandbekämpfung",
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(),
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        var operation2 = new OperationResponse(
            UUID.randomUUID(),
            "Einsatz 2",
            "Hilfeleistung",
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(),
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(operationRepositoryStub.findAll()).thenReturn(List.of(mapToEntity(operation1), mapToEntity(operation2)));

        // Act
        var result = sut.getAllOperations();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Einsatz 1");
        assertThat(result.get(1).title()).isEqualTo("Einsatz 2");
    }

    @Test
    void shouldAssignEquipmentToOperation() {
        // Arrange
        var operationId = UUID.randomUUID();
        var equipmentRequest = new AssignEquipmentRequest(List.of(UUID.randomUUID()));
        var updatedOperation = new OperationResponse(
            operationId,
            "Einsatz Feuerwehr",
            "Brandbekämpfung",
            LocalDateTime.now(),
            LocalDateTime.now(),
            equipmentRequest.equipmentIds(),
            "Brand in Bürogebäude",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(operationRepositoryStub.findById(operationId)).thenReturn(Optional.of(mapToEntity(updatedOperation)));
        when(operationRepositoryStub.save(any())).thenReturn(mapToEntity(updatedOperation));

        // Act
        var result = sut.assignEquipment(operationId, equipmentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(operationId);
        verify(operationRepositoryStub, times(1)).findById(operationId);
        verify(operationRepositoryStub, times(1)).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenNoEquipmentAvailable() {
        // Arrange
        var operationType = "Brandbekämpfung";

        // Act
        var result = sut.getAvailableEquipment(operationType);

        // Assert
        assertThat(result).isEmpty();
    }

    private Operation mapToEntity(OperationResponse response) {
        var entity = new Operation();
        entity.setId(response.id());
        entity.setTitle(response.title());
        entity.setOperationType(response.operationType());
        entity.setStartTime(response.startTime());
        entity.setEndTime(response.endTime());
        entity.setRequiredEquipment(response.requiredEquipment());
        entity.setDescription(response.description());
        entity.setCreatedAt(response.createdAt());
        entity.setUpdatedAt(response.updatedAt());
        return entity;
    }
}