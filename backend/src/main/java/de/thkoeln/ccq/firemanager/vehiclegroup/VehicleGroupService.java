package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupConflictException;
import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class VehicleGroupService {

    private final VehicleGroupRepository vehicleGroupRepository;

    public VehicleGroupService(VehicleGroupRepository vehicleGroupRepository) {
        this.vehicleGroupRepository = vehicleGroupRepository;
    }

    public VehicleGroup create(String name, String beschreibung) {
        VehicleGroup vehicleGroup = new VehicleGroup(name, beschreibung);
        return this.vehicleGroupRepository.save(vehicleGroup);
    }

    public VehicleGroup create(String name) {
        return this.create(name, null);
    }

    public Page<VehicleGroup> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return this.vehicleGroupRepository.findByArchivedFalse(pageable);
    }

    public VehicleGroup getById(UUID vehicleGroupId) {
        if (vehicleGroupId == null) {
            throw new IllegalArgumentException("vehicleGroupId must not be null");
        }
        return vehicleGroupRepository.findById(vehicleGroupId)
                .filter(vg -> !vg.isArchived())
                .orElseThrow(() -> new VehicleGroupNotFoundException(vehicleGroupId));
    }

    public void deleteById(UUID vehicleGroupId) {
        VehicleGroup vehicleGroup = getById(vehicleGroupId);
        vehicleGroup.setArchived(true);
        vehicleGroup.setUpdatedAt(OffsetDateTime.now());
        this.vehicleGroupRepository.save(vehicleGroup);
    }

    public void deleteByIdWithConflictCheck(UUID vehicleGroupId, boolean hasNonArchivedVehicles) {
        if (hasNonArchivedVehicles) {
            throw new VehicleGroupConflictException(
                    "VehicleGroup with id " + vehicleGroupId
                            + " cannot be deleted because it still has"
                            + " non-archived vehicles"
            );
        }
        deleteById(vehicleGroupId);
    }

    public VehicleGroup update(UUID vehicleGroupId, String name, String beschreibung) {
        VehicleGroup existingVehicleGroup = getById(vehicleGroupId);

        if (name != null && !name.isBlank()) {
            existingVehicleGroup.setName(name);
        }
        existingVehicleGroup.setBeschreibung(beschreibung);
        existingVehicleGroup.setUpdatedAt(OffsetDateTime.now());

        return this.vehicleGroupRepository.save(existingVehicleGroup);
    }
}
