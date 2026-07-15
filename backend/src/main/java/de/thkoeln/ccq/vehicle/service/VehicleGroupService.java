package de.thkoeln.ccq.vehicle.service;

import de.thkoeln.ccq.vehicle.VehicleGroup;
import de.thkoeln.ccq.vehicle.VehicleGroupRepository;
import de.thkoeln.ccq.vehicle.VehicleRepository;
import de.thkoeln.ccq.vehicle.dto.VehicleGroupRequest;
import de.thkoeln.ccq.vehicle.exception.VehicleGroupHasVehiclesException;
import de.thkoeln.ccq.vehicle.exception.VehicleGroupNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<VehicleGroup> getAllVehicleGroups() {
        return vehicleGroupRepository.findByArchiviertFalse();
    }

    public VehicleGroup getVehicleGroupById(UUID id) {
        return vehicleGroupRepository.findByArchiviertFalseAndId(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new VehicleGroupNotFoundException(id));
    }

    public VehicleGroup updateVehicleGroup(UUID id, VehicleGroupRequest request) {
        VehicleGroup existingVehicleGroup = getVehicleGroupById(id);
        existingVehicleGroup.setName(request.getName());
        existingVehicleGroup.setBeschreibung(request.getBeschreibung());
        return vehicleGroupRepository.save(existingVehicleGroup);
    }

    public void deleteVehicleGroup(UUID id) {
        VehicleGroup vehicleGroup = getVehicleGroupById(id);
        if (vehicleRepository.existsByGruppeAndArchiviertFalse(vehicleGroup)) {
            throw new VehicleGroupHasVehiclesException(id);
        }
        vehicleGroup.setArchiviert(true);
        vehicleGroupRepository.save(vehicleGroup);
    }
}
