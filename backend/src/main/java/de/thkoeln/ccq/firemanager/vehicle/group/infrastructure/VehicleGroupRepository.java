package de.thkoeln.ccq.firemanager.vehicle.group.infrastructure;

import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VehicleGroupRepository extends JpaRepository<VehicleGroup, UUID> {
    @Query("SELECT v FROM VehicleGroup v WHERE v.archived = false")
    List<VehicleGroup> findAllNotArchived();

    @Query("SELECT v FROM VehicleGroup v WHERE v.id = :id AND v.archived = false")
    VehicleGroup findByIdNotArchived(@Param("id") UUID id);
}