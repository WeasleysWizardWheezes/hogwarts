package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleConflictException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroupService;
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

    public Vehicle create(String name, String callSign, String licensePlate, 
            int yearOfConstruction, String description, Vehicle.VehicleStatus status, 
            UUID vehicleGroupId) {
        if (vehicleRepository.existsByLicensePlate(licensePlate)) {
            throw new VehicleConflictException(
                    "Vehicle with license plate " + licensePlate + " already exists");
        }

        VehicleGroup vehicleGroup = vehicleGroupService.getById(vehicleGroupId);
        Vehicle vehicle = new Vehicle(name, callSign, licensePlate, 
                yearOfConstruction, description, status, vehicleGroup);
        return this.vehicleRepository.save(vehicle);
    }

    public Vehicle create(String name, String callSign, String licensePlate, 
            int yearOfConstruction, UUID vehicleGroupId) {
        return this.create(name, callSign, licensePlate, yearOfConstruction, 
                null, Vehicle.VehicleStatus.AVAILABLE, vehicleGroupId);
    }

    public List<Vehicle> getAll() {
        return this.vehicleRepository.findAll();
    }

    public List<Vehicle> getByVehicleGroupId(UUID vehicleGroupId) {
        return this.vehicleRepository.findByVehicleGroupId(vehicleGroupId);
    }

    public List<Vehicle> getByVehicleGroupIdAndStatus(UUID vehicleGroupId, Vehicle.VehicleStatus status) {
        return this.vehicleRepository.findByVehicleGroupIdAndStatus(vehicleGroupId, status);
    }

    public Vehicle getById(UUID vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId must not be null");
        }
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    public Vehicle update(UUID vehicleId, String name, String callSign, 
            String licensePlate, int yearOfConstruction, String description, 
            Vehicle.VehicleStatus status, UUID vehicleGroupId) {
        Vehicle existing = getById(vehicleId);
        
        if (!existing.getLicensePlate().equals(licensePlate) 
                && vehicleRepository.existsByLicensePlate(licensePlate)) {
            throw new VehicleConflictException(
                    "Vehicle with license plate " + licensePlate + " already exists");
        }

        VehicleGroup vehicleGroup = vehicleGroupService.getById(vehicleGroupId);
        existing.update(name, callSign, licensePlate, yearOfConstruction, description, status, vehicleGroup);
        return this.vehicleRepository.save(existing);
    }

    public void deleteById(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }
        vehicleRepository.deleteById(vehicleId);
    }

    public boolean existsByLicensePlate(String licensePlate) {
        return vehicleRepository.existsByLicensePlate(licensePlate);
    }
}