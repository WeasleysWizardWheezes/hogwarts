package de.thkoeln.ccq.firemanager.vehicle.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleGroupRepository extends JpaRepository<VehicleGroup, UUID> {
    
    // Find all non-archived vehicle groups
    List<VehicleGroup> findByIsArchivedFalse();
    
    // Find by ID and non-archived
    Optional<VehicleGroup> findByIdAndIsArchivedFalse(UUID id);
}