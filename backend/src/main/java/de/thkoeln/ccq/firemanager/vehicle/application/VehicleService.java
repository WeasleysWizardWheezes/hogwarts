package de.thkoeln.ccq.firemanager.vehicle.application;

import de.thkoeln.ccq.firemanager.vehicle.domain.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.domain.VehicleStatus;
import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.infrastructure.VehicleRepository;
import de.thkoeln.ccq.firemanager.vehicle.group.infrastructure.VehicleGroupRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {
    private final VehicleRepository repository;
    private final VehicleGroupRepository groupRepository;

    public VehicleService(VehicleRepository repository, VehicleGroupRepository groupRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    public Vehicle create(Vehicle vehicle) {
        VehicleGroup group = groupRepository.findByIdNotArchived(vehicle.getVehicleGroup().getId());
        if (group == null) {
            throw new RuntimeException("VehicleGroup not found");
        }
        vehicle.setErstellzeitpunkt(Instant.now());
        vehicle.setUpdatezeitpunkt(Instant.now());
        vehicle.setVehicleGroup(group);
        return repository.save(vehicle);
    }

    public List<Vehicle> getAll() {
        return repository.findAllNotArchived();
    }

    public List<Vehicle> getByStatus(VehicleStatus status) {
        return repository.findByStatusNotArchived(status);
    }

    public List<Vehicle> getByVehicleGroup(UUID vehicleGroupId) {
        return repository.findByVehicleGroupIdNotArchived(vehicleGroupId);
    }

    public Vehicle getById(UUID id) {
        return repository.findByIdNotArchived(id);
    }

    public Vehicle update(UUID id, Vehicle vehicle) {
        Vehicle existing = repository.findByIdNotArchived(id);
        if (existing == null) {
            throw new RuntimeException("Vehicle not found");
        }
        VehicleGroup group = groupRepository.findByIdNotArchived(vehicle.getVehicleGroup().getId());
        if (group == null) {
            throw new RuntimeException("VehicleGroup not found");
        }
        existing.setName(vehicle.getName());
        existing.setFunkrufname(vehicle.getFunkrufname());
        existing.setKennzeichen(vehicle.getKennzeichen());
        existing.setBaujahr(vehicle.getBaujahr());
        existing.setBeschreibung(vehicle.getBeschreibung());
        existing.setStatus(vehicle.getStatus());
        existing.setVehicleGroup(group);
        existing.setUpdatezeitpunkt(Instant.now());
        return repository.save(existing);
    }

    public void archive(UUID id) {
        Vehicle vehicle = repository.findByIdNotArchived(id);
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found");
        }
        vehicle.setArchived(true);
        repository.save(vehicle);
    }
}