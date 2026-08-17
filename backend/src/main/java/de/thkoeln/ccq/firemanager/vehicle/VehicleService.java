package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleGroupService vehicleGroupService;

    public VehicleService(
            VehicleRepository vehicleRepository,
            VehicleGroupService vehicleGroupService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleGroupService = vehicleGroupService;
    }

    public Vehicle create(
            String name,
            String funkrufname,
            String kennzeichen,
            Integer baujahr,
            String beschreibung,
            VehicleStatus status,
            UUID vehicleGroupId
    ) {
        VehicleGroup vehicleGroup = this.vehicleGroupService.getById(vehicleGroupId);

        if (status == null) {
            status = VehicleStatus.VERFUEGBAR;
        }

        Vehicle vehicle = new Vehicle(name, funkrufname, kennzeichen, baujahr, beschreibung, status, vehicleGroup);
        return this.vehicleRepository.save(vehicle);
    }

    public Page<Vehicle> getAll(int page, int size, UUID vehicleGroupId, VehicleStatus status) {
        Pageable pageable = PageRequest.of(page, size);

        if (vehicleGroupId != null && status != null) {
            return this.vehicleRepository
                    .findByVehicleGroupIdAndStatusAndArchivedFalse(
                            vehicleGroupId, status, pageable);
        }
        if (vehicleGroupId != null) {
            return this.vehicleRepository.findByVehicleGroupIdAndArchivedFalse(vehicleGroupId, pageable);
        }
        if (status != null) {
            return this.vehicleRepository.findByStatusAndArchivedFalse(status, pageable);
        }
        return this.vehicleRepository.findByArchivedFalse(pageable);
    }

    public boolean hasNonArchivedVehiclesInGroup(UUID vehicleGroupId) {
        return this.vehicleRepository.existsByVehicleGroupIdAndArchivedFalse(vehicleGroupId);
    }

    public Vehicle getById(UUID vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId must not be null");
        }
        return vehicleRepository.findById(vehicleId)
                .filter(v -> !v.isArchived())
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    public void deleteById(UUID vehicleId) {
        Vehicle vehicle = getById(vehicleId);
        vehicle.setArchived(true);
        vehicle.setUpdatedAt(OffsetDateTime.now());
        this.vehicleRepository.save(vehicle);
    }

    public Vehicle update(
            UUID vehicleId,
            String name,
            String funkrufname,
            String kennzeichen,
            Integer baujahr,
            String beschreibung,
            VehicleStatus status,
            UUID vehicleGroupId
    ) {
        Vehicle existingVehicle = getById(vehicleId);

        if (name != null && !name.isBlank()) {
            existingVehicle.setName(name);
        }
        if (funkrufname != null && !funkrufname.isBlank()) {
            existingVehicle.setFunkrufname(funkrufname);
        }
        if (kennzeichen != null && !kennzeichen.isBlank()) {
            existingVehicle.setKennzeichen(kennzeichen);
        }
        existingVehicle.setBaujahr(baujahr);
        existingVehicle.setBeschreibung(beschreibung);

        if (status != null) {
            existingVehicle.setStatus(status);
        }
        if (vehicleGroupId != null) {
            VehicleGroup vehicleGroup = this.vehicleGroupService.getById(vehicleGroupId);
            existingVehicle.setVehicleGroup(vehicleGroup);
        }

        existingVehicle.setUpdatedAt(OffsetDateTime.now());
        return this.vehicleRepository.save(existingVehicle);
    }
}
