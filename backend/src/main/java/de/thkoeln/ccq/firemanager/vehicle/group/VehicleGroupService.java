package de.thkoeln.ccq.firemanager.vehicle.group;

import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.VehicleRepository;
import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import jakarta.persistence.EntityNotFoundException;
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
                .isArchived(false)
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
        return vehicleGroupRepository.findByIdAndIsArchivedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Fahrzeuggruppe mit ID " + id + " nicht gefunden"));
    }

    public VehicleGroup updateVehicleGroup(UUID id, VehicleGroupUpdateRequest request) {
        VehicleGroup existingGroup = getVehicleGroupById(id);
        
        if (existingGroup.isArchived()) {
            throw new IllegalStateException("Archivierte Fahrzeuggruppe kann nicht bearbeitet werden");
        }
        
        existingGroup.setName(request.getName());
        existingGroup.setBeschreibung(request.getBeschreibung());
        
        return vehicleGroupRepository.save(existingGroup);
    }

    public void archiveVehicleGroup(UUID id) {
        VehicleGroup vehicleGroup = getVehicleGroupById(id);
        
        // Check if there are any non-archived vehicles in this group
        List<Vehicle> activeVehicles = vehicleRepository.findByVehicleGroupIdAndIsArchivedFalse(id);
        if (!activeVehicles.isEmpty()) {
            throw new IllegalStateException("Fahrzeuggruppe kann nicht archiviert werden, da noch aktive Fahrzeuge vorhanden sind");
        }
        
        vehicleGroup.setArchived(true);
        vehicleGroupRepository.save(vehicleGroup);
    }

    public List<Vehicle> getVehiclesByGroupId(UUID groupId) {
        return vehicleRepository.findByVehicleGroupIdAndIsArchivedFalse(groupId);
    }
}