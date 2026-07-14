package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEquipmentRepository extends JpaRepository<EquipmentJpaEntity, UUID> {

    @Query("SELECT e FROM EquipmentJpaEntity e WHERE e.location = :location")
    List<EquipmentJpaEntity> findByLocation(String location);

    @Query("SELECT e FROM EquipmentJpaEntity e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<EquipmentJpaEntity> searchByNameOrSerialNumber(String searchTerm);

    boolean existsBySerialNumber(String serialNumber);
}