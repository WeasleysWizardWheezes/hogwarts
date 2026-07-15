package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroupService;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import org.springframework.stereotype.Service;

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

    public Vehicle create(
            String name,
            String funkrufname,
            String kennzeichen,
            Integer baujahr,
            String beschreibung,
            VehicleStatus status,
            UUID gruppeId
    ) {
        VehicleGroup gruppe = vehicleGroupService.getById(gruppeId);
        Vehicle vehicle = new Vehicle(
                name,
                funkrufname,
                kennzeichen,
                baujahr,
                beschreibung,
                status,
                gruppe
        );
        return this.vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAll() {
        return this.vehicleRepository.findByArchiviertFalse();
    }

    public List<Vehicle> getByStatus(VehicleStatus status) {
        return this.vehicleRepository.findByArchiviertFalseAndStatus(status);
    }

    public List<Vehicle> getByGruppeId(UUID gruppeId) {
        return this.vehicleRepository.findByArchiviertFalseAndGruppeId(gruppeId);
    }

    public Vehicle getById(UUID vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId must not be null");
        }
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    public Vehicle update(
            UUID vehicleId,
            String name,
            String funkrufname,
            String kennzeichen,
            Integer baujahr,
            String beschreibung,
            VehicleStatus status,
            UUID gruppeId
    ) {
        Vehicle vehicle = getById(vehicleId);
        VehicleGroup gruppe = vehicleGroupService.getById(gruppeId);
        vehicle.update(
                name,
                funkrufname,
                kennzeichen,
                baujahr,
                beschreibung,
                status,
                gruppe
        );
        return this.vehicleRepository.save(vehicle);
    }

    public void deleteById(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }
        
        Vehicle vehicle = getById(vehicleId);
        vehicle.archive();
        this.vehicleRepository.save(vehicle);
    }
}