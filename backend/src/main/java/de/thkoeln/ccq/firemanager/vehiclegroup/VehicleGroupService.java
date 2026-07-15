package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.VehicleRepository;
import org.springframework.stereotype.Service;

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
        VehicleGroup vehicleGroup = getById(vehicleGroupId);
        vehicleGroup.update(name, beschreibung);
        return this.vehicleGroupRepository.save(vehicleGroup);
    }

    public void deleteById(UUID vehicleGroupId) {
        if (!vehicleGroupRepository.existsById(vehicleGroupId)) {
            throw new VehicleGroupNotFoundException(vehicleGroupId);
        }
        
        boolean hasVehicles = vehicleRepository.existsByGruppeIdAndArchiviertFalse(vehicleGroupId);
        if (hasVehicles) {
            throw new IllegalStateException("Cannot delete vehicle group with associated vehicles");
        }
        
        VehicleGroup vehicleGroup = getById(vehicleGroupId);
        vehicleGroup.archive();
        this.vehicleGroupRepository.save(vehicleGroup);
    }
}