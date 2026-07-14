package de.thkoeln.ccq.firemanager.equipment.application;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentApplicationService {

    private final EquipmentRepository equipmentRepository;

    @Transactional
    public Equipment createEquipment(
            String name,
            String serialNumber,
            String type,
            String location,
            String description
    ) {
        if (equipmentRepository.existsBySerialNumber(serialNumber)) {
            throw new EquipmentAlreadyExistsException(serialNumber);
        }
        
        return equipmentRepository.save(
                Equipment.create(name, serialNumber, type, location, description)
        );
    }

    @Transactional(readOnly = true)
    public Optional<Equipment> getEquipment(EquipmentId id) {
        return equipmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Equipment> searchEquipment(String searchTerm, String location) {
        if (searchTerm != null && !searchTerm.isBlank()) {
            return equipmentRepository.searchByNameOrSerialNumber(searchTerm);
        }
        
        if (location != null && !location.isBlank()) {
            return equipmentRepository.findByLocation(location);
        }
        
        return equipmentRepository.findAll();
    }

    @Transactional
    public Equipment updateEquipment(
            EquipmentId id,
            String name,
            String serialNumber,
            String type,
            String location,
            String description
    ) {
        var existingEquipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
        
        // Check if serial number is being changed to an existing one
        if (!existingEquipment.serialNumber().equals(serialNumber) && 
                equipmentRepository.existsBySerialNumber(serialNumber)) {
            throw new EquipmentAlreadyExistsException(serialNumber);
        }
        
        var updatedEquipment = existingEquipment.withUpdatedFields(
                name, serialNumber, type, location, description
        );
        
        return equipmentRepository.save(updatedEquipment);
    }

    @Transactional
    public void deleteEquipment(EquipmentId id) {
        if (!equipmentRepository.findById(id).isPresent()) {
            throw new EquipmentNotFoundException(id);
        }
        
        equipmentRepository.delete(id);
    }
}