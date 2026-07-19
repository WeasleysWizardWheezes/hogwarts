package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupConflictException;
import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<VehicleGroup> getAll() {
        return this.vehicleGroupRepository.findByArchiviertFalse();
    }

    public VehicleGroup getById(UUID vehicleGroupId) {
        if (vehicleGroupId == null) {
            throw new IllegalArgumentException("vehicleGroupId must not be null");
        }
        return vehicleGroupRepository.findById(vehicleGroupId)
                .filter(vg -> !vg.isArchiviert())
                .orElseThrow(() -> new VehicleGroupNotFoundException(vehicleGroupId));
    }

    public VehicleGroup update(UUID vehicleGroupId, String name, String beschreibung) {
        VehicleGroup existing = getById(vehicleGroupId);
        existing.setName(name);
        if (beschreibung != null) {
            existing.setBeschreibung(beschreibung);
        }
        return this.vehicleGroupRepository.save(existing);
    }

    public void deleteById(UUID vehicleGroupId) {
        VehicleGroup existing = getById(vehicleGroupId);
        existing.setArchiviert(true);
        this.vehicleGroupRepository.save(existing);
    }

    public void deleteByIdWithConflictCheck(UUID vehicleGroupId) {
        if (!vehicleGroupRepository.existsByIdAndArchiviertFalse(vehicleGroupId)) {
            throw new VehicleGroupNotFoundException(vehicleGroupId);
        }
        // Hier würde später die Prüfung auf zugeordnete Fahrzeuge kommen
        // Für jetzt: Soft Delete
        VehicleGroup existing = getById(vehicleGroupId);
        existing.setArchiviert(true);
        this.vehicleGroupRepository.save(existing);
    }
}