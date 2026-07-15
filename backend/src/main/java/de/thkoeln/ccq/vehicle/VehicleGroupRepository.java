package de.thkoeln.ccq.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleGroupRepository extends JpaRepository<VehicleGroup, UUID> {

    List<VehicleGroup> findByArchiviertFalse();

    List<VehicleGroup> findByArchiviertFalseAndId(UUID id);
}
