package de.thkoeln.ccq.firemanager.equipment.service;

import de.thkoeln.ccq.firemanager.equipment.model.Equipment;
import de.thkoeln.ccq.firemanager.equipment.repository.EquipmentRepository;
import de.thkoeln.ccq.firemanager.equipment.web.EquipmentDto;
import de.thkoeln.ccq.firemanager.equipment.web.EquipmentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentMapper equipmentMapper;

    @InjectMocks
    private EquipmentService equipmentService;

    @Test
    void createEquipment_shouldCreateAndReturnEquipment() {
        // Given
        EquipmentDto equipmentDto = new EquipmentDto(null, "Test Equipment", "TEST123");
        Equipment equipment = new Equipment(null, "Test Equipment", "TEST123");
        Equipment savedEquipment = new Equipment(UUID.randomUUID(), "Test Equipment", "TEST123");
        EquipmentDto expectedDto = new EquipmentDto(savedEquipment.getId(), "Test Equipment", "TEST123");
        
        when(equipmentRepository.existsBySerialNumber("TEST123")).thenReturn(false);
        when(equipmentMapper.toEntity(equipmentDto)).thenReturn(equipment);
        when(equipmentRepository.save(equipment)).thenReturn(savedEquipment);
        when(equipmentMapper.toDto(savedEquipment)).thenReturn(expectedDto);

        // When
        EquipmentDto result = equipmentService.createEquipment(equipmentDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getSerialNumber(), result.getSerialNumber());
    }

    @Test
    void createEquipment_shouldThrowExceptionWhenSerialNumberExists() {
        // Given
        EquipmentDto equipmentDto = new EquipmentDto(null, "Test Equipment", "TEST123");
        
        when(equipmentRepository.existsBySerialNumber("TEST123")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            equipmentService.createEquipment(equipmentDto);
        });
        
        assertEquals("Equipment with serial number TEST123 already exists", exception.getMessage());
    }

    @Test
    void getEquipmentById_shouldReturnEquipment() {
        // Given
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "Test Equipment", "TEST123");
        EquipmentDto expectedDto = new EquipmentDto(equipmentId, "Test Equipment", "TEST123");
        
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(equipmentMapper.toDto(equipment)).thenReturn(expectedDto);

        // When
        EquipmentDto result = equipmentService.getEquipmentById(equipmentId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getSerialNumber(), result.getSerialNumber());
    }

    @Test
    void getEquipmentById_shouldThrowExceptionWhenNotFound() {
        // Given
        UUID equipmentId = UUID.randomUUID();
        
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            equipmentService.getEquipmentById(equipmentId);
        });
        
        assertEquals("Equipment with id " + equipmentId + " not found", exception.getMessage());
    }
}