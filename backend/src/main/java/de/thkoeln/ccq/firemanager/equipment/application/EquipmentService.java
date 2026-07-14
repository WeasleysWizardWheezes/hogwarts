package de.thkoeln.ccq.firemanager.equipment.application;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public Equipment createEquipment(
        String name,
        String serialNumber,
        String type,
        String storageLocation,
        String description,
        String acquisitionDate,
        String manufacturer
    ) {
        return equipmentRepository.save(Equipment.create(
            name,
            serialNumber,
            type,
            storageLocation,
            description,
            acquisitionDate != null ? java.time.LocalDate.parse(acquisitionDate) : null,
            manufacturer
        ));
    }

    public Optional<Equipment> getEquipment(EquipmentId id) {
        return equipmentRepository.findById(id);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> searchEquipmentByName(String name) {
        return equipmentRepository.findByName(name);
    }

    public List<Equipment> searchEquipmentBySerialNumber(String serialNumber) {
        return equipmentRepository.findBySerialNumber(serialNumber);
    }

    public List<Equipment> searchEquipmentByStorageLocation(String storageLocation) {
        return equipmentRepository.findByStorageLocation(storageLocation);
    }

    public void deleteEquipment(EquipmentId id) {
        equipmentRepository.delete(id);
    }
}
