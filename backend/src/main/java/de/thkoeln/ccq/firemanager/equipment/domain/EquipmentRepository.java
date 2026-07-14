package de.thkoeln.ccq.firemanager.equipment.domain;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {
    Equipment save(Equipment equipment);
    Optional<Equipment> findById(EquipmentId id);
    List<Equipment> findAll();
    List<Equipment> findByName(String name);
    List<Equipment> findBySerialNumber(String serialNumber);
    List<Equipment> findByStorageLocation(String storageLocation);
    void delete(EquipmentId id);
}
