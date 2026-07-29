package de.thkoeln.ccq.firemanager.vehicle.application;

import de.thkoeln.ccq.firemanager.vehicle.domain.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.domain.VehicleStatus;
import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.infrastructure.VehicleRepository;
import de.thkoeln.ccq.firemanager.vehicle.group.infrastructure.VehicleGroupRepository;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleUpdateRequest;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleArchivedException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupArchivedException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
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

    public Vehicle create(VehicleRequest request) {
        VehicleGroup group = groupRepository.findByIdNotArchived(request.getVehicleGroupId());
        if (group == null) {
            throw new VehicleGroupNotFoundException(request.getVehicleGroupId());
        }
        if (group.isArchived()) {
            throw new VehicleGroupArchivedException(request.getVehicleGroupId());
        }
        if (request.getBaujahr() != null) {
            int currentYear = Year.now().getValue();
            if (request.getBaujahr() < 1900 || request.getBaujahr() > currentYear) {
                throw new IllegalArgumentException("Baujahr muss zwischen 1900 und " + currentYear + " liegen");
            }
        }
        VehicleStatus status = request.getStatus() != null ? request.getStatus() : VehicleStatus.VERFUEGBAR;
        Vehicle vehicle = Vehicle.builder()
                .name(request.getName())
                .funkrufname(request.getFunkrufname())
                .kennzeichen(request.getKennzeichen())
                .baujahr(request.getBaujahr())
                .beschreibung(request.getBeschreibung())
                .status(status)
                .erstellzeitpunkt(Instant.now())
                .updatezeitpunkt(Instant.now())
                .archived(false)
                .vehicleGroup(group)
                .build();
        return repository.save(vehicle);
    }

    public List<Vehicle> getAll(VehicleStatus status, UUID vehicleGroupId, Boolean isArchived) {
        if (isArchived != null && isArchived) {
            if (status != null) {
                return repository.findByStatusArchived(status);
            } else if (vehicleGroupId != null) {
                return repository.findByVehicleGroupIdArchived(vehicleGroupId);
            } else {
                return repository.findAllArchived();
            }
        } else {
            if (status != null) {
                return repository.findByStatusNotArchived(status);
            } else if (vehicleGroupId != null) {
                return repository.findByVehicleGroupIdNotArchived(vehicleGroupId);
            } else {
                return repository.findAllNotArchived();
            }
        }
    }

    public Vehicle getById(UUID id) {
        Vehicle vehicle = repository.findByIdNotArchived(id);
        if (vehicle == null) {
            throw new VehicleNotFoundException(id);
        }
        return vehicle;
    }

    public Vehicle update(UUID id, VehicleUpdateRequest request) {
        Vehicle existing = repository.findByIdNotArchived(id);
        if (existing == null) {
            throw new VehicleNotFoundException(id);
        }
        if (existing.isArchived()) {
            throw new VehicleArchivedException(id);
        }
        VehicleGroup group = null;
        if (request.getVehicleGroupId() != null) {
            group = groupRepository.findByIdNotArchived(request.getVehicleGroupId());
            if (group == null) {
                throw new VehicleGroupNotFoundException(request.getVehicleGroupId());
            }
            if (group.isArchived()) {
                throw new VehicleGroupArchivedException(request.getVehicleGroupId());
            }
        }
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getFunkrufname() != null) {
            existing.setFunkrufname(request.getFunkrufname());
        }
        if (request.getKennzeichen() != null) {
            existing.setKennzeichen(request.getKennzeichen());
        }
        if (request.getBaujahr() != null) {
            int currentYear = Year.now().getValue();
            if (request.getBaujahr() < 1900 || request.getBaujahr() > currentYear) {
                throw new IllegalArgumentException("Baujahr muss zwischen 1900 und " + currentYear + " liegen");
            }
            existing.setBaujahr(request.getBaujahr());
        }
        if (request.getBeschreibung() != null) {
            existing.setBeschreibung(request.getBeschreibung());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (group != null) {
            existing.setVehicleGroup(group);
        }
        existing.setUpdatezeitpunkt(Instant.now());
        return repository.save(existing);
    }

    public void archive(UUID id) {
        Vehicle vehicle = repository.findByIdNotArchived(id);
        if (vehicle == null) {
            throw new VehicleNotFoundException(id);
        }
        if (vehicle.isArchived()) {
            throw new VehicleArchivedException(id);
        }
        vehicle.setArchived(true);
        repository.save(vehicle);
    }
}