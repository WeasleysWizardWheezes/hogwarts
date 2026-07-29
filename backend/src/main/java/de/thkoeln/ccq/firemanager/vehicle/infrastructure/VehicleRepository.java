package de.thkoeln.ccq.firemanager.vehicle.infrastructure;

import de.thkoeln.ccq.firemanager.vehicle.domain.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.domain.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    @Query("SELECT v FROM Vehicle v WHERE v.archived = false")
    List<Vehicle> findAllNotArchived();

    @Query("SELECT v FROM Vehicle v WHERE v.archived = true")
    List<Vehicle> findAllArchived();

    @Query("SELECT v FROM Vehicle v WHERE v.id = :id AND v.archived = false")
    Vehicle findByIdNotArchived(@Param("id") UUID id);

    @Query("SELECT v FROM Vehicle v WHERE v.archived = false AND v.status = :status")
    List<Vehicle> findByStatusNotArchived(@Param("status") VehicleStatus status);

    @Query("SELECT v FROM Vehicle v WHERE v.archived = true AND v.status = :status")
    List<Vehicle> findByStatusArchived(@Param("status") VehicleStatus status);

    @Query("SELECT v FROM Vehicle v WHERE v.archived = false AND v.vehicleGroup.id = :vehicleGroupId")
    List<Vehicle> findByVehicleGroupIdNotArchived(@Param("vehicleGroupId") UUID vehicleGroupId);

    @Query("SELECT v FROM Vehicle v WHERE v.archived = true AND v.vehicleGroup.id = :vehicleGroupId")
    List<Vehicle> findByVehicleGroupIdArchived(@Param("vehicleGroupId") UUID vehicleGroupId);
}