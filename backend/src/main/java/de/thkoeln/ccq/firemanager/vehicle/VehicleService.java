package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroupService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
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

    public Vehicle create(String name, String funkrufname, String kennzeichen, Integer baujahr, 
                         String beschreibung, VehicleStatus status, UUID gruppeId) {
        VehicleGroup gruppe = vehicleGroupService.getById(gruppeId);
        Vehicle vehicle = new Vehicle(name, funkrufname, kennzeichen, baujahr, beschreibung, status, gruppe);
        return this.vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAll() {
        return this.vehicleRepository.findByArchiviertFalse();
    }

    public List<Vehicle> getByStatus(VehicleStatus status) {
        return this.vehicleRepository.findByStatusAndArchiviertFalse(status);
    }

    public List<Vehicle> getByGruppeId(UUID gruppeId) {
        return this.vehicleRepository.findByGruppeIdAndArchiviertFalse(gruppeId);
    }

    public Vehicle getById(UUID vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId must not be null");
        }
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    public Vehicle update(UUID vehicleId, String name, String funkrufname, String kennzeichen, 
                         Integer baujahr, String beschreibung, VehicleStatus status) {
        Vehicle existingVehicle = getById(vehicleId);
        if (name != null) {
            existingVehicle.setName(name);
        }
        if (funkrufname != null) {
            existingVehicle.setFunkrufname(funkrufname);
        }
        if (kennzeichen != null) {
            existingVehicle.setKennzeichen(kennzeichen);
        }
        if (baujahr != null) {
            existingVehicle.setBaujahr(baujahr);
        }
        if (beschreibung != null) {
            existingVehicle.setBeschreibung(beschreibung);
        }
        if (status != null) {
            existingVehicle.setStatus(status);
        }
        existingVehicle.setUpdatezeitpunkt(Instant.now());
        return this.vehicleRepository.save(existingVehicle);
    }

    public void deleteById(UUID vehicleId) {
        Vehicle existingVehicle = getById(vehicleId);
        // Soft delete - mark as archived
        existingVehicle.setArchiviert(true);
        existingVehicle.setUpdatezeitpunkt(Instant.now());
        this.vehicleRepository.save(existingVehicle);
    }

    public boolean existsById(UUID vehicleId) {
        return vehicleRepository.existsById(vehicleId);
    }
}