package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    List<Vehicle> findByNameContainingIgnoreCase(String name);

    List<Vehicle> findByLicensePlateContainingIgnoreCase(String licensePlate);

    List<Vehicle> findByVehicleGroupId(UUID vehicleGroupId);

    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);
}