package de.thkoeln.ccq.firemanager;

import de.thkoeln.ccq.firemanager.dto.AssignEquipmentRequest;
import de.thkoeln.ccq.firemanager.dto.CreateOperationRequest;
import de.thkoeln.ccq.firemanager.dto.OperationResponse;
import de.thkoeln.ccq.firemanager.repository.OperationRepository;
import de.thkoeln.ccq.firemanager.service.OperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationServiceTest {

    private OperationService sut;

    private CreateOperationRequest validRequest;

    @BeforeEach
    void setup() {
        sut = new OperationService();
        
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
    void shouldThrowWhenRepositoryIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> sut.createOperation(validRequest))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldGetAllOperationsWhenRepositoryIsNull() {
        // Act
        var result = sut.getAllOperations();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetAvailableEquipment() {
        // Arrange
        var operationType = "Brandbekämpfung";

        // Act
        var result = sut.getAvailableEquipment(operationType);

        // Assert
        assertThat(result).isEmpty();
    }
}