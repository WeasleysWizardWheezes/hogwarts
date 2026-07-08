package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.AssignEquipmentRequest;
import de.thkoeln.ccq.firemanager.dto.CreateOperationRequest;
import de.thkoeln.ccq.firemanager.dto.OperationResponse;
import de.thkoeln.ccq.firemanager.model.Operation;
import de.thkoeln.ccq.firemanager.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private OperationService sut;

    private CreateOperationRequest validRequest;

    @BeforeEach
    void setup() {
        sut = new OperationService();
        sut.setOperationRepository(operationRepositoryStub);
        
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
    }

    @Test
    void shouldCreateOperationSuccessfully() {
        // Arrange
        when(operationRepositoryStub.save(any())).thenReturn(mapToEntity(validResponse()));

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
        var response = validResponse();
        response.id(operationId);
        when(operationRepositoryStub.findById(operationId)).thenReturn(Optional.of(mapToEntity(response)));

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
        var operation1 = validResponse();
        var operation2 = validResponse();
        operation2.title("Einsatz 2");
        when(operationRepositoryStub.findAll()).thenReturn(List.of(mapToEntity(operation1), mapToEntity(operation2)));

        // Act
        var result = sut.getAllOperations();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Einsatz Feuerwehr");
        assertThat(result.get(1).title()).isEqualTo("Einsatz 2");
    }

    @Test
    void shouldAssignEquipmentToOperation() {
        // Arrange
        var operationId = UUID.randomUUID();
        var equipmentRequest = new AssignEquipmentRequest(List.of(UUID.randomUUID()));
        var updatedResponse = validResponse();
        when(operationRepositoryStub.findById(operationId)).thenReturn(Optional.of(mapToEntity(updatedResponse)));
        when(operationRepositoryStub.save(any())).thenReturn(mapToEntity(updatedResponse));

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

    private OperationResponse validResponse() {
        return new OperationResponse(
            UUID.randomUUID(),
            "Einsatz Feuerwehr",
            "Brandbekämpfung",
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(3),
            List.of(UUID.randomUUID()),
            "Brand in Bürogebäude",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
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