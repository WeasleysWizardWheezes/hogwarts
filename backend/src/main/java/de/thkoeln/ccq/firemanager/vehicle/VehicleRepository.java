package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findByIsArchivedFalse();
    List<Vehicle> findByIsArchived(boolean isArchived);
    List<Vehicle> findByVehicleGroupIdAndIsArchivedFalse(UUID vehicleGroupId);
    List<Vehicle> findByStatusAndIsArchivedFalse(VehicleStatus status);
    List<Vehicle> findByVehicleGroupIdAndIsArchived(UUID vehicleGroupId, boolean isArchived);
    List<Vehicle> findByStatusAndIsArchived(VehicleStatus status, boolean isArchived);
}