package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.dto.CreateVehicleRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.UpdateVehicleRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request
    ) {
        var vehicle = vehicleService.create(
                request.name(),
                request.radioCallName(),
                request.licensePlate(),
                request.year(),
                request.description(),
                request.status(),
                request.vehicleGroupId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAll() {
        var vehicles = vehicleService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(vehicles);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(
            @PathVariable UUID vehicleId
    ) {
        Vehicle vehicle = this.vehicleService.getById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }

    @PatchMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request
    ) {
        var updatedVehicle = vehicleService.update(
                vehicleId,
                request.name(),
                request.radioCallName(),
                request.licensePlate(),
                request.year(),
                request.description(),
                request.status()
        );
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable UUID vehicleId
    ) {
        this.vehicleService.deleteById(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchByName(@RequestParam String name) {
        var vehicles = vehicleService.searchByName(name);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search-by-license-plate")
    public ResponseEntity<List<Vehicle>> searchByLicensePlate(@RequestParam String licensePlate) {
        var vehicles = vehicleService.searchByLicensePlate(licensePlate);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/by-group/{vehicleGroupId}")
    public ResponseEntity<List<Vehicle>> getByVehicleGroupId(@PathVariable UUID vehicleGroupId) {
        var vehicles = vehicleService.getByVehicleGroupId(vehicleGroupId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<Vehicle>> getByStatus(@RequestParam Vehicle.VehicleStatus status) {
        var vehicles = vehicleService.getByStatus(status);
        return ResponseEntity.ok(vehicles);
    }
}