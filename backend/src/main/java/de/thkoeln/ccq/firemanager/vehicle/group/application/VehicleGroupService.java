package de.thkoeln.ccq.firemanager.vehicle.group.application;

import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.group.infrastructure.VehicleGroupRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleGroupService {
    private final VehicleGroupRepository repository;

    public VehicleGroupService(VehicleGroupRepository repository) {
        this.repository = repository;
    }

    public VehicleGroup create(VehicleGroup group) {
        group.setErstellzeitpunkt(Instant.now());
        group.setUpdatezeitpunkt(Instant.now());
        return repository.save(group);
    }

    public List<VehicleGroup> getAll() {
        return repository.findAllNotArchived();
    }

    public VehicleGroup getById(UUID id) {
        return repository.findByIdNotArchived(id);
    }

    public VehicleGroup update(UUID id, VehicleGroup group) {
        VehicleGroup existing = repository.findByIdNotArchived(id);
        if (existing == null) {
            throw new RuntimeException("VehicleGroup not found");
        }
        existing.setName(group.getName());
        existing.setBeschreibung(group.getBeschreibung());
        existing.setUpdatezeitpunkt(Instant.now());
        return repository.save(existing);
    }

    public void archive(UUID id) {
        VehicleGroup group = repository.findByIdNotArchived(id);
        if (group == null) {
            throw new RuntimeException("VehicleGroup not found");
        }
        group.setArchived(true);
        repository.save(group);
    }
}