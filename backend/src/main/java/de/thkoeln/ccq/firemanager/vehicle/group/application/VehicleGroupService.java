package de.thkoeln.ccq.firemanager.vehicle.group.application;

import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.group.infrastructure.VehicleGroupRepository;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupUpdateRequest;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupArchivedException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupNotFoundException;
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

    public VehicleGroup create(VehicleGroupRequest request) {
        VehicleGroup group = VehicleGroup.builder()
                .name(request.getName())
                .beschreibung(request.getBeschreibung())
                .erstellzeitpunkt(Instant.now())
                .updatezeitpunkt(Instant.now())
                .archived(false)
                .build();
        return repository.save(group);
    }

    public List<VehicleGroup> getAll(Boolean isArchived) {
        if (isArchived == null) {
            return repository.findAllNotArchived();
        } else if (isArchived) {
            return repository.findAllArchived();
        } else {
            return repository.findAllNotArchived();
        }
    }

    public VehicleGroup getById(UUID id) {
        VehicleGroup group = repository.findByIdNotArchived(id);
        if (group == null) {
            throw new VehicleGroupNotFoundException(id);
        }
        return group;
    }

    public VehicleGroup update(UUID id, VehicleGroupUpdateRequest request) {
        VehicleGroup existing = repository.findByIdNotArchived(id);
        if (existing == null) {
            throw new VehicleGroupNotFoundException(id);
        }
        if (existing.isArchived()) {
            throw new VehicleGroupArchivedException(id);
        }
        existing.setName(request.getName());
        existing.setBeschreibung(request.getBeschreibung());
        existing.setUpdatezeitpunkt(Instant.now());
        return repository.save(existing);
    }

    public void archive(UUID id) {
        VehicleGroup group = repository.findByIdNotArchived(id);
        if (group == null) {
            throw new VehicleGroupNotFoundException(id);
        }
        if (group.isArchived()) {
            throw new VehicleGroupArchivedException(id);
        }
        group.setArchived(true);
        repository.save(group);
    }
}