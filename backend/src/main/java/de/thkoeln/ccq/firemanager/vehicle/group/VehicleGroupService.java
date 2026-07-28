package de.thkoeln.ccq.firemanager.vehicle.group;

import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.VehicleRepository;
import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupUpdateRequest;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupArchivedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleGroupService {

    private final VehicleGroupRepository vehicleGroupRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleGroup createVehicleGroup(VehicleGroupRequest request) {
        VehicleGroup vehicleGroup = VehicleGroup.builder()
                .name(request.getName())
                .beschreibung(request.getBeschreibung())
                .build();
        return vehicleGroupRepository.save(vehicleGroup);
    }

    public List<VehicleGroup> getAllVehicleGroups(boolean includeArchived) {
        if (includeArchived) {
            return vehicleGroupRepository.findAll();
        }
        return vehicleGroupRepository.findByIsArchivedFalse();
    }

    public VehicleGroup getVehicleGroupById(UUID id) {
        return vehicleGroupRepository.findById(id)
                .orElseThrow(() -> new VehicleGroupNotFoundException(id));
    }

    @Transactional
    public VehicleGroup updateVehicleGroup(UUID id, VehicleGroupUpdateRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupRepository.findById(id)
                .orElseThrow(() -> new VehicleGroupNotFoundException(id));

        if (vehicleGroup.isArchived()) {
            throw new VehicleGroupArchivedException(id);
        }

        if (request.getName() != null) {
            vehicleGroup.setName(request.getName());
        }
        if (request.getBeschreibung() != null) {
            vehicleGroup.setBeschreibung(request.getBeschreibung());
        }

        vehicleGroup.setUpdatezeitpunkt(Instant.now());
        return vehicleGroupRepository.save(vehicleGroup);
    }

    @Transactional
    public void archiveVehicleGroup(UUID id) {
        VehicleGroup vehicleGroup = vehicleGroupRepository.findById(id)
                .orElseThrow(() -> new VehicleGroupNotFoundException(id));

        if (vehicleGroup.isArchived()) {
            return;
        }

        // Archive all vehicles in this group
        List<Vehicle> vehicles = vehicleRepository.findByVehicleGroupIdAndIsArchivedFalse(id);
        for (Vehicle vehicle : vehicles) {
            vehicle.setArchived(true);
            vehicleRepository.save(vehicle);
        }

        vehicleGroup.setArchived(true);
        vehicleGroupRepository.save(vehicleGroup);
    }
}