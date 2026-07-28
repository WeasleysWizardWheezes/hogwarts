package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleUpdateRequest;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleArchivedException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleGroupRepository vehicleGroupRepository;

    public Vehicle createVehicle(VehicleRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupRepository.findById(request.getVehicleGroupId())
                .orElseThrow(() -> new VehicleGroupNotFoundException(request.getVehicleGroupId()));

        if (request.getBaujahr() != null && (request.getBaujahr() < 1900 || request.getBaujahr() > Year.now().getValue())) {
            throw new IllegalArgumentException("Baujahr muss zwischen 1900 und " + Year.now().getValue() + " liegen");
        }

        Vehicle vehicle = Vehicle.builder()
                .name(request.getName())
                .funkrufname(request.getFunkrufname())
                .kennzeichen(request.getKennzeichen())
                .baujahr(request.getBaujahr())
                .beschreibung(request.getBeschreibung())
                .status(request.getStatus() != null ? request.getStatus() : VehicleStatus.VERFUEGBAR)
                .vehicleGroup(vehicleGroup)
                .build();

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles(VehicleStatus status, UUID vehicleGroupId, boolean includeArchived) {
        if (includeArchived) {
            if (status != null && vehicleGroupId != null) {
                return vehicleRepository.findByStatusAndIsArchived(status, true);
            } else if (status != null) {
                return vehicleRepository.findByStatusAndIsArchived(status, true);
            } else if (vehicleGroupId != null) {
                return vehicleRepository.findByVehicleGroupIdAndIsArchived(vehicleGroupId, true);
            } else {
                return vehicleRepository.findByIsArchived(true);
            }
        } else {
            if (status != null && vehicleGroupId != null) {
                return vehicleRepository.findByStatusAndIsArchivedFalse(status);
            } else if (status != null) {
                return vehicleRepository.findByStatusAndIsArchivedFalse(status);
            } else if (vehicleGroupId != null) {
                return vehicleRepository.findByVehicleGroupIdAndIsArchivedFalse(vehicleGroupId);
            } else {
                return vehicleRepository.findByIsArchivedFalse();
            }
        }
    }

    public Vehicle getVehicleById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @Transactional
    public Vehicle updateVehicle(UUID id, VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));

        if (vehicle.isArchived()) {
            throw new VehicleArchivedException(id);
        }

        if (request.getName() != null) {
            vehicle.setName(request.getName());
        }
        if (request.getFunkrufname() != null) {
            vehicle.setFunkrufname(request.getFunkrufname());
        }
        if (request.getKennzeichen() != null) {
            vehicle.setKennzeichen(request.getKennzeichen());
        }
        if (request.getBaujahr() != null) {
            if (request.getBaujahr() < 1900 || request.getBaujahr() > Year.now().getValue()) {
                throw new IllegalArgumentException("Baujahr muss zwischen 1900 und " + Year.now().getValue() + " liegen");
            }
            vehicle.setBaujahr(request.getBaujahr());
        }
        if (request.getBeschreibung() != null) {
            vehicle.setBeschreibung(request.getBeschreibung());
        }
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }
        if (request.getVehicleGroupId() != null) {
            VehicleGroup vehicleGroup = vehicleGroupRepository.findById(request.getVehicleGroupId())
                    .orElseThrow(() -> new VehicleGroupNotFoundException(request.getVehicleGroupId()));
            vehicle.setVehicleGroup(vehicleGroup);
        }

        vehicle.setUpdatezeitpunkt(Instant.now());
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public void archiveVehicle(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));

        if (vehicle.isArchived()) {
            return;
        }

        vehicle.setArchived(true);
        vehicleRepository.save(vehicle);
    }
}