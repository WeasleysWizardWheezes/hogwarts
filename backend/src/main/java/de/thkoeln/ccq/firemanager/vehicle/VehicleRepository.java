package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    List<Vehicle> findByArchiviertFalse();

    List<Vehicle> findByArchiviertFalseAndStatus(VehicleStatus status);

    List<Vehicle> findByArchiviertFalseAndGruppeId(UUID gruppeId);

    boolean existsByGruppeIdAndArchiviertFalse(UUID gruppeId);
}