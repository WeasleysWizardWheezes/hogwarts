package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupConflictException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleGroupService {

    private final VehicleGroupRepository vehicleGroupRepository;

    public VehicleGroupService(VehicleGroupRepository vehicleGroupRepository) {
        this.vehicleGroupRepository = vehicleGroupRepository;
    }

    public VehicleGroup create(String name, String description) {
        Boolean groupExists = this.vehicleGroupRepository.existsByName(name);
        if (groupExists) {
            throw new VehicleGroupConflictException("Vehicle group with name '" + name + "' already exists");
        }
        VehicleGroup vehicleGroup = new VehicleGroup(name, description);
        return this.vehicleGroupRepository.save(vehicleGroup);
    }

    public VehicleGroup create(String name) {
        return this.create(name, null);
    }

    public List<VehicleGroup> getAll() {
        return this.vehicleGroupRepository.findAll();
    }

    public VehicleGroup getById(UUID vehicleGroupId) {
        if (vehicleGroupId == null) {
            throw new IllegalArgumentException("vehicleGroupId must not be null");
        }
        return vehicleGroupRepository.findById(vehicleGroupId)
                .orElseThrow(() -> new VehicleGroupNotFoundException(vehicleGroupId));
    }

    public VehicleGroup update(UUID vehicleGroupId, String name, String description) {
        VehicleGroup existingGroup = getById(vehicleGroupId);
        existingGroup.update(name, description);
        return this.vehicleGroupRepository.save(existingGroup);
    }

    public void deleteById(UUID vehicleGroupId) {
        if (!vehicleGroupRepository.existsById(vehicleGroupId)) {
            throw new VehicleGroupNotFoundException(vehicleGroupId);
        }
        vehicleGroupRepository.deleteById(vehicleGroupId);
    }

    public List<VehicleGroup> searchByName(String name) {
        return this.vehicleGroupRepository.findByNameContainingIgnoreCase(name);
    }
}