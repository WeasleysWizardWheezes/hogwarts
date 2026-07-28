package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleUpdateRequest;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleArchivedException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) UUID vehicleGroupId,
            @RequestParam(required = false, defaultValue = "false") boolean isArchived) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles(status, vehicleGroupId, isArchived);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }

    @PatchMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleService.updateVehicle(vehicleId, request);
        return ResponseEntity.ok(vehicle);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> archiveVehicle(@PathVariable UUID vehicleId) {
        vehicleService.archiveVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<String> handleVehicleNotFoundException(VehicleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(VehicleArchivedException.class)
    public ResponseEntity<String> handleVehicleArchivedException(VehicleArchivedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(VehicleGroupNotFoundException.class)
    public ResponseEntity<String> handleVehicleGroupNotFoundException(VehicleGroupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}