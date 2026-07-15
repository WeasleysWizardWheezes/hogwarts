package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByGruppeId(UUID gruppeId);

    boolean existsByGruppeId(UUID gruppeId);
}
