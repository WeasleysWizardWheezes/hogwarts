package de.thkoeln.ccq.firemanager.vehiclegroup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleGroupRepository extends JpaRepository<VehicleGroup, UUID> {
}
