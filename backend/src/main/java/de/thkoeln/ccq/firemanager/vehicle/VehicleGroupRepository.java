package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleGroupRepository extends JpaRepository<VehicleGroup, UUID> {

    List<VehicleGroup> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}