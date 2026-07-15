package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleGroupRepository vehicleGroupRepository;

    public Vehicle createVehicle(VehicleRequest request) {
        // Validate year
        if (request.getBaujahr() != null) {
            int currentYear = Year.now().getValue();
            if (request.getBaujahr() < 1900 || request.getBaujahr() > currentYear) {
                throw new IllegalArgumentException("Baujahr muss zwischen 1900 und " + currentYear + " liegen");
            }
        }

        VehicleGroup vehicleGroup = vehicleGroupRepository.findByIdAndIsArchivedFalse(request.getVehicleGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Fahrzeuggruppe mit ID " + request.getVehicleGroupId() + " nicht gefunden"));

        Vehicle vehicle = Vehicle.builder()
                .name(request.getName())
                .funkrufname(request.getFunkrufname())
                .kennzeichen(request.getKennzeichen())
                .baujahr(request.getBaujahr())
                .beschreibung(request.getBeschreibung())
                .status(request.getStatus())
                .isArchived(false)
                .vehicleGroup(vehicleGroup)
                .build();

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles(VehicleStatus status, UUID vehicleGroupId, boolean includeArchived) {
        if (includeArchived) {
            if (status != null && vehicleGroupId != null) {
                return vehicleRepository.findByVehicleGroupIdAndStatus(vehicleGroupId, status);
            } else if (status != null) {
                return vehicleRepository.findByStatus(status);
            } else if (vehicleGroupId != null) {
                return vehicleRepository.findByVehicleGroupId(vehicleGroupId);
            } else {
                return vehicleRepository.findAll();
            }
        } else {
            if (status != null && vehicleGroupId != null) {
                return vehicleRepository.findByVehicleGroupIdAndStatusAndIsArchivedFalse(vehicleGroupId, status);
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
        return vehicleRepository.findByIdAndIsArchivedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Fahrzeug mit ID " + id + " nicht gefunden"));
    }

    public Vehicle updateVehicle(UUID id, VehicleUpdateRequest request) {
        Vehicle existingVehicle = getVehicleById(id);
        
        if (existingVehicle.isArchived()) {
            throw new IllegalStateException("Archiviertes Fahrzeug kann nicht bearbeitet werden");
        }

        // Validate year
        if (request.getBaujahr() != null) {
            int currentYear = Year.now().getValue();
            if (request.getBaujahr() < 1900 || request.getBaujahr() > currentYear) {
                throw new IllegalArgumentException("Baujahr muss zwischen 1900 und " + currentYear + " liegen");
            }
        }

        VehicleGroup vehicleGroup = vehicleGroupRepository.findByIdAndIsArchivedFalse(request.getVehicleGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Fahrzeuggruppe mit ID " + request.getVehicleGroupId() + " nicht gefunden"));

        existingVehicle.setName(request.getName());
        existingVehicle.setFunkrufname(request.getFunkrufname());
        existingVehicle.setKennzeichen(request.getKennzeichen());
        existingVehicle.setBaujahr(request.getBaujahr());
        existingVehicle.setBeschreibung(request.getBeschreibung());
        existingVehicle.setStatus(request.getStatus());
        existingVehicle.setVehicleGroup(vehicleGroup);

        return vehicleRepository.save(existingVehicle);
    }

    public void archiveVehicle(UUID id) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setArchived(true);
        vehicleRepository.save(vehicle);
    }
}