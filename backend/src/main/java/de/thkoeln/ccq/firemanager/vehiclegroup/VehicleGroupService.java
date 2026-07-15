package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.VehicleRepository;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleConflictException;
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

    public VehicleGroup update(UUID vehicleGroupId, String name, String beschreibung) {
        VehicleGroup existing = getById(vehicleGroupId);
        existing.setName(name);
        existing.setBeschreibung(beschreibung);
        existing.setUpdatezeitpunkt(Instant.now());
        return this.vehicleGroupRepository.save(existing);
    }

    public void deleteById(UUID vehicleGroupId) {
        if (!vehicleGroupRepository.existsById(vehicleGroupId)) {
            throw new VehicleGroupNotFoundException(vehicleGroupId);
        }
        if (vehicleRepository.existsByGruppeId(vehicleGroupId)) {
            throw new VehicleConflictException("Cannot delete vehicle group with assigned vehicles");
        }
        vehicleGroupRepository.deleteById(vehicleGroupId);
    }
}
