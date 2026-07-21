package de.thkoeln.ccq.firemanager.vehicle;

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

    public Vehicle create(String name, String radioCallName, String licensePlate, 
                        Integer year, String description, Vehicle.VehicleStatus status, 
                        UUID vehicleGroupId) {
        VehicleGroup vehicleGroup = vehicleGroupService.getById(vehicleGroupId);
        Vehicle vehicle = new Vehicle(name, radioCallName, licensePlate, year, description, status, vehicleGroup);
        return this.vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAll() {
        return this.vehicleRepository.findAll();
    }

    public Vehicle getById(UUID vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId must not be null");
        }
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    public Vehicle update(UUID vehicleId, String name, String radioCallName, 
                         String licensePlate, Integer year, String description, 
                         Vehicle.VehicleStatus status) {
        Vehicle existingVehicle = getById(vehicleId);
        existingVehicle.update(name, radioCallName, licensePlate, year, description, status);
        return this.vehicleRepository.save(existingVehicle);
    }

    public void deleteById(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }
        vehicleRepository.deleteById(vehicleId);
    }

    public List<Vehicle> searchByName(String name) {
        return this.vehicleRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Vehicle> searchByLicensePlate(String licensePlate) {
        return this.vehicleRepository.findByLicensePlateContainingIgnoreCase(licensePlate);
    }

    public List<Vehicle> getByVehicleGroupId(UUID vehicleGroupId) {
        return this.vehicleRepository.findByVehicleGroupId(vehicleGroupId);
    }

    public List<Vehicle> getByStatus(Vehicle.VehicleStatus status) {
        return this.vehicleRepository.findByStatus(status);
    }
}