package de.thkoeln.ccq.firemanager.equipment.service;

import de.thkoeln.ccq.firemanager.equipment.model.Equipment;
import de.thkoeln.ccq.firemanager.equipment.repository.EquipmentRepository;
import de.thkoeln.ccq.firemanager.equipment.web.EquipmentDto;
import de.thkoeln.ccq.firemanager.equipment.web.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional
    public EquipmentDto createEquipment(EquipmentDto equipmentDto) {
        if (equipmentRepository.existsBySerialNumber(equipmentDto.getSerialNumber())) {
            throw new IllegalArgumentException("Equipment with serial number " + equipmentDto.getSerialNumber() + " already exists");
        }

        Equipment equipment = equipmentMapper.toEntity(equipmentDto);
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return equipmentMapper.toDto(savedEquipment);
    }

    @Transactional(readOnly = true)
    public EquipmentDto getEquipmentById(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment with id " + id + " not found"));
        return equipmentMapper.toDto(equipment);
    }
}