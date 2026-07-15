package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupConflictException;
import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleGroupService {

    private final VehicleGroupRepository vehicleGroupRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleGroupService(
            VehicleGroupRepository vehicleGroupRepository,
            VehicleRepository vehicleRepository
    ) {
        this.vehicleGroupRepository = vehicleGroupRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleGroup create(String name, String beschreibung) {
        VehicleGroup vehicleGroup = new VehicleGroup(name, beschreibung);
        return this.vehicleGroupRepository.save(vehicleGroup);
    }

    public VehicleGroup create(String name) {
        return this.create(name, null);
    }

    public List<VehicleGroup> getAll() {
        return this.vehicleGroupRepository.findByArchiviertFalse();
    }

    public VehicleGroup getById(UUID vehicleGroupId) {
        if (vehicleGroupId == null) {
            throw new IllegalArgumentException("vehicleGroupId must not be null");
        }
        return vehicleGroupRepository.findById(vehicleGroupId)
                .orElseThrow(() -> new VehicleGroupNotFoundException(vehicleGroupId));
    }

    public VehicleGroup update(UUID vehicleGroupId, String name, String beschreibung) {
        VehicleGroup existingGroup = getById(vehicleGroupId);
        if (name != null) {
            existingGroup.setName(name);
        }
        if (beschreibung != null) {
            existingGroup.setBeschreibung(beschreibung);
        }
        existingGroup.setUpdatezeitpunkt(Instant.now());
        return this.vehicleGroupRepository.save(existingGroup);
    }

    public void deleteById(UUID vehicleGroupId) {
        VehicleGroup existingGroup = getById(vehicleGroupId);
        
        // Check if there are any vehicles assigned to this group
        if (hasVehicles(vehicleGroupId)) {
            throw new VehicleGroupConflictException("Cannot delete vehicle group with assigned vehicles");
        }
        
        // Soft delete - mark as archived
        existingGroup.setArchiviert(true);
        existingGroup.setUpdatezeitpunkt(Instant.now());
        this.vehicleGroupRepository.save(existingGroup);
    }

    public boolean existsById(UUID vehicleGroupId) {
        return vehicleGroupRepository.existsById(vehicleGroupId);
    }

    public boolean hasVehicles(UUID vehicleGroupId) {
        VehicleGroup group = getById(vehicleGroupId);
        // Use the vehicle repository to check if any vehicles belong to this group
        return vehicleRepository.existsByGruppeId(vehicleGroupId);
    }
}