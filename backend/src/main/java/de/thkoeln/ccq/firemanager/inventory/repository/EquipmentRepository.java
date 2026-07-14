package de.thkoeln.ccq.firemanager.inventory.repository;

import de.thkoeln.ccq.firemanager.inventory.domain.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {
    
    Optional<Equipment> findBySerialNumber(String serialNumber);
    
    List<Equipment> findByNameContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(String name, String serialNumber);
    
    List<Equipment> findByLocation(String location);
}