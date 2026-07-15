package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    
    // Find all non-archived vehicles
    List<Vehicle> findByIsArchivedFalse();
    
    // Find by ID and non-archived
    Optional<Vehicle> findByIdAndIsArchivedFalse(UUID id);
    
    // Find by vehicle group ID and non-archived
    List<Vehicle> findByVehicleGroupIdAndIsArchivedFalse(UUID vehicleGroupId);
    
    // Find by status and non-archived
    List<Vehicle> findByStatusAndIsArchivedFalse(VehicleStatus status);
    
    // Find by vehicle group ID, status and non-archived
    List<Vehicle> findByVehicleGroupIdAndStatusAndIsArchivedFalse(UUID vehicleGroupId, VehicleStatus status);
}