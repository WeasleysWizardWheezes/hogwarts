package de.thkoeln.ccq.firemanager.equipment.controller;

import de.thkoeln.ccq.firemanager.equipment.service.EquipmentService;
import de.thkoeln.ccq.firemanager.equipment.web.EquipmentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EquipmentControllerTest {

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private EquipmentController equipmentController;

    @Test
    void createEquipment_shouldReturnCreatedStatus() {
        // Given
        EquipmentDto equipmentDto = new EquipmentDto(null, "Test Equipment", "TEST123");
        EquipmentDto createdEquipment = new EquipmentDto(UUID.randomUUID(), "Test Equipment", "TEST123");
        
        when(equipmentService.createEquipment(any(EquipmentDto.class))).thenReturn(createdEquipment);

        // When
        ResponseEntity<EquipmentDto> response = equipmentController.createEquipment(equipmentDto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdEquipment, response.getBody());
    }

    @Test
    void getEquipmentById_shouldReturnOkStatus() {
        // Given
        UUID equipmentId = UUID.randomUUID();
        EquipmentDto equipmentDto = new EquipmentDto(equipmentId, "Test Equipment", "TEST123");
        
        when(equipmentService.getEquipmentById(equipmentId)).thenReturn(equipmentDto);

        // When
        ResponseEntity<EquipmentDto> response = equipmentController.getEquipmentById(equipmentId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(equipmentDto, response.getBody());
    }
}