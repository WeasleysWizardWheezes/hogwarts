package de.thkoeln.ccq.firemanager.equipment.domain;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {
    Equipment save(Equipment equipment);
    
    Optional<Equipment> findById(EquipmentId id);
    
    List<Equipment> findAll();
    
    List<Equipment> findByLocation(String location);
    
    List<Equipment> searchByNameOrSerialNumber(String searchTerm);
    
    void delete(EquipmentId id);
    
    boolean existsBySerialNumber(String serialNumber);
}