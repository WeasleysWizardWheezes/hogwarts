package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEquipmentRepository extends JpaRepository<EquipmentJpaEntity, UUID> {
    List<EquipmentJpaEntity> findByNameContainingIgnoreCase(String name);
    List<EquipmentJpaEntity> findBySerialNumberContainingIgnoreCase(String serialNumber);
    List<EquipmentJpaEntity> findByStorageLocationContainingIgnoreCase(String storageLocation);
}
