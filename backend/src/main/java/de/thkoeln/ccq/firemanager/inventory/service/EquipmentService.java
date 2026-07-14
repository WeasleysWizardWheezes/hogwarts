package de.thkoeln.ccq.firemanager.inventory.service;

import de.thkoeln.ccq.firemanager.inventory.domain.Equipment;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentCreateDTO;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentDTO;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentUpdateDTO;
import de.thkoeln.ccq.firemanager.inventory.mapper.EquipmentMapper;
import de.thkoeln.ccq.firemanager.inventory.repository.EquipmentRepository;
import de.thkoeln.ccq.firemanager.shared.exception.ResourceNotFoundException;
import de.thkoeln.ccq.firemanager.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;
    
    @Transactional(readOnly = true)
    public List<EquipmentDTO> getAllEquipment(String search, String location) {
        if (search != null && !search.isEmpty()) {
            return equipmentRepository.findByNameContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(search, search)
                    .stream()
                    .map(equipmentMapper::toDTO)
                    .collect(Collectors.toList());
        }
        
        if (location != null && !location.isEmpty()) {
            return equipmentRepository.findByLocation(location)
                    .stream()
                    .map(equipmentMapper::toDTO)
                    .collect(Collectors.toList());
        }
        
        return equipmentRepository.findAll()
                .stream()
                .map(equipmentMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public EquipmentDTO getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        return equipmentMapper.toDTO(equipment);
    }
    
    @Transactional
    public EquipmentDTO createEquipment(EquipmentCreateDTO equipmentCreateDTO) {
        // Check if equipment with same serial number already exists
        if (equipmentRepository.findBySerialNumber(equipmentCreateDTO.getSerialNumber()).isPresent()) {
            throw new ValidationException("Equipment with serial number " + equipmentCreateDTO.getSerialNumber() + " already exists");
        }
        
        Equipment equipment = equipmentMapper.toEntity(equipmentCreateDTO);
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return equipmentMapper.toDTO(savedEquipment);
    }
    
    @Transactional
    public EquipmentDTO updateEquipment(Long id, EquipmentUpdateDTO equipmentUpdateDTO) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        
        // Check if another equipment has the same serial number
        if (equipmentUpdateDTO.getSerialNumber() != null && 
            !equipmentUpdateDTO.getSerialNumber().equals(equipment.getSerialNumber())) {
            if (equipmentRepository.findBySerialNumber(equipmentUpdateDTO.getSerialNumber()).isPresent()) {
                throw new ValidationException("Equipment with serial number " + equipmentUpdateDTO.getSerialNumber() + " already exists");
            }
        }
        
        equipmentMapper.updateEntityFromDTO(equipmentUpdateDTO, equipment);
        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return equipmentMapper.toDTO(updatedEquipment);
    }
    
    @Transactional
    public void deleteEquipment(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + id);
        }
        equipmentRepository.deleteById(id);
    }
}