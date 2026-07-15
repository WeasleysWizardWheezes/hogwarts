package de.thkoeln.ccq.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    List<Vehicle> findByArchiviertFalse();

    List<Vehicle> findByArchiviertFalseAndId(UUID id);

    List<Vehicle> findByArchiviertFalseAndStatus(VehicleStatus status);

    List<Vehicle> findByArchiviertFalseAndGruppe(VehicleGroup gruppe);

    boolean existsByGruppeAndArchiviertFalse(VehicleGroup gruppe);

    boolean existsByKennzeichenAndArchiviertFalse(String kennzeichen);
}
