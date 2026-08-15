package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleConflictException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleInUseException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroupService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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
            String radioCallName,
            String licensePlate,
            Integer yearOfConstruction,
            String description,
            Vehicle.VehicleStatus status,
            UUID vehicleGroupId
    ) {
        VehicleGroup vehicleGroup = vehicleGroupService.getById(vehicleGroupId);
        Vehicle vehicle = new Vehicle(
                name,
                radioCallName,
                licensePlate,
                yearOfConstruction,
                description,
                status,
                vehicleGroup
        );
        return this.vehicleRepository.save(vehicle);
    }

    public Vehicle create(String name, Vehicle.VehicleStatus status, UUID vehicleGroupId) {
        return this.create(name, null, null, null, null, status, vehicleGroupId);
    }

    public List<Vehicle> getAll() {
        return this.vehicleRepository.findAll();
    }

    public List<Vehicle> getByVehicleGroupId(UUID vehicleGroupId) {
        return this.vehicleRepository.findByVehicleGroupId(vehicleGroupId);
    }

    public List<Vehicle> getByStatus(Vehicle.VehicleStatus status) {
        return this.vehicleRepository.findByStatus(status);
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

    public Vehicle update(
            UUID vehicleId,
            String name,
            String radioCallName,
            String licensePlate,
            Integer yearOfConstruction,
            String description,
            Vehicle.VehicleStatus status,
            UUID vehicleGroupId
    ) {
        Vehicle vehicle = getById(vehicleId);
        VehicleGroup vehicleGroup = vehicleGroupService.getById(vehicleGroupId);
        vehicle.setName(name);
        vehicle.setRadioCallName(radioCallName);
        vehicle.setLicensePlate(licensePlate);
        vehicle.setYearOfConstruction(yearOfConstruction);
        vehicle.setDescription(description);
        vehicle.setStatus(status);
        vehicle.setVehicleGroup(vehicleGroup);
        vehicle.setUpdatedAt(OffsetDateTime.now());
        return this.vehicleRepository.save(vehicle);
    }

    public void deleteById(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }
        Vehicle vehicle = getById(vehicleId);
        if (vehicle.getStatus() == Vehicle.VehicleStatus.IM_EINSATZ) {
            throw new VehicleInUseException();
        }
        vehicle.setArchived(true);
        vehicle.setUpdatedAt(OffsetDateTime.now());
        this.vehicleRepository.save(vehicle);
    }
}
