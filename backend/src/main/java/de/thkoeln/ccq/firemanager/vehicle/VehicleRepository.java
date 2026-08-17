package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Page<Vehicle> findByArchivedFalse(Pageable pageable);

    Page<Vehicle> findByVehicleGroupIdAndArchivedFalse(UUID vehicleGroupId, Pageable pageable);

    Page<Vehicle> findByStatusAndArchivedFalse(VehicleStatus status, Pageable pageable);

    Page<Vehicle> findByVehicleGroupIdAndStatusAndArchivedFalse(
            UUID vehicleGroupId, VehicleStatus status, Pageable pageable);

    boolean existsByVehicleGroupIdAndArchivedFalse(UUID vehicleGroupId);
}
