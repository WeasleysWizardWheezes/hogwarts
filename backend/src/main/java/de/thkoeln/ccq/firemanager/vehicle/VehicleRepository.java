package de.thkoeln.ccq.firemanager.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    List<Vehicle> findByArchiviertFalse();

    List<Vehicle> findByStatusAndArchiviertFalse(VehicleStatus status);

    List<Vehicle> findByGruppeIdAndArchiviertFalse(UUID gruppeId);

    boolean existsByIdAndArchiviertFalse(UUID id);

}